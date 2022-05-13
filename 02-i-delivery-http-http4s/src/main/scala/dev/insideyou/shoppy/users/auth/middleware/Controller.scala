package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.syntax.all._
import dev.profunktor.auth._
import org.http4s.server.AuthMiddleware
import pdi.jwt.JwtClaim

object ControllerImpl {
  def make[F[_]: MonadThrow](
      boundary: Boundary[F, CommonUser, jwt.JwtAuth, jwt.JwtToken]
  ): Controller.Middleware[F, CommonUser] =
    new Controller.Middleware[F, CommonUser] {
      override lazy val middleware: F[AuthMiddleware[F, CommonUser]] =
        boundary.authMiddleware.map { m =>
          JwtAuthMiddleware(
            jwtAuth = m.auth,
            authenticate = adapt(m.find)
          )
        }

      private def adapt[A](
          in: jwt.JwtToken => F[Option[A]]
      ): jwt.JwtToken => JwtClaim => F[Option[A]] =
        profunktorJwtToken => _ => in(profunktorJwtToken)
    }
}
