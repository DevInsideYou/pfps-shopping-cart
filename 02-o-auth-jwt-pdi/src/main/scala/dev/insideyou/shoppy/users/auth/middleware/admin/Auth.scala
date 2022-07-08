package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats._
import cats.syntax.all._
import dev.profunktor.auth.jwt
import eu.timepit.refined.auto._
import io.circe.parser.decode

import CirceCodecs._

object AuthImpl {
  def make[F[_]: MonadThrow]: Auth[F, jwt.JwtAuth, jwt.JwtToken] =
    new Auth[F, jwt.JwtAuth, jwt.JwtToken] {
      override def token(adminKey: AdminUserTokenConfig): F[jwt.JwtToken] =
        jwt.JwtToken(adminKey.secret).pure

      override def auth(tokenKey: JwtSecretKeyConfig): F[jwt.JwtAuth] =
        jwt.JwtAuth
          .hmac(tokenKey.secret.value, pdi.jwt.JwtAlgorithm.HS256)
          .pure
          .widen

      override def claim(token: jwt.JwtToken, auth: jwt.JwtAuth): F[ClaimContent] =
        for {
          jwtClaim     <- jwt.jwtDecode(token, auth)
          claimContent <- ApplicativeThrow[F].fromEither(decode(jwtClaim.content))
        } yield claimContent
    }
}
