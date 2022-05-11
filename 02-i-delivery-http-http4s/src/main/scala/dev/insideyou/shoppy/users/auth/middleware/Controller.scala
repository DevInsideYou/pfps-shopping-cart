package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats.syntax.all._
import org.http4s.server.AuthMiddleware
import pdi.jwt.JwtClaim
import dev.profunktor.auth.JwtAuthMiddleware
import cats.MonadThrow
import dev.profunktor.auth.jwt
import pdi.jwt.JwtAlgorithm

// TODO decide again whether we should create a parametric JwtToken instead of a DTO
object ControllerImpl {
  def make[F[_]: MonadThrow](
      boundary: Boundary[F, CommonUser]
  ): Controller.Middleware[F, CommonUser] =
    new Controller.Middleware[F, CommonUser] {
      override lazy val middleware: F[AuthMiddleware[F, CommonUser]] =
        boundary.authMiddleware.map { authMiddleware =>
          JwtAuthMiddleware(
            jwtAuth = userJwtAuth(authMiddleware.tokenKeyConfig),
            authenticate = adapt(authMiddleware.find)
          )
        }

      private def adapt[A](
          in: JwtToken => F[Option[A]]
      ): dev.profunktor.auth.jwt.JwtToken => JwtClaim => F[Option[A]] =
        profunktorJwtToken => _ => in(JwtToken(profunktorJwtToken.value))

      def userJwtAuth(tokenConfig: JwtAccessTokenKeyConfig) =
        jwt.JwtAuth
          .hmac(
            tokenConfig.secret.value,
            JwtAlgorithm.HS256
          )
    }
}
