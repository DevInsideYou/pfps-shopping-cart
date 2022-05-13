package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats._
import cats.syntax.all._
import dev.profunktor.auth._
import org.http4s.server.AuthMiddleware
import pdi.jwt.JwtClaim

object ControllerImpl {
  def make[F[_]: MonadThrow](
      boundary: Boundary[F, AdminUser, jwt.JwtAuth, jwt.JwtToken]
  ): Controller.Middleware[F, AdminUser] =
    new Controller.Middleware[F, AdminUser] {
      override lazy val middleware: F[AuthMiddleware[F, AdminUser]] =
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
