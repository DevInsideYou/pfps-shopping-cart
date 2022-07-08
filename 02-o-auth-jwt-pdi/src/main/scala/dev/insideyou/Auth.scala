package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.syntax.all._
import dev.profunktor.auth.jwt

object AuthImpl {
  def make[F[_]: Applicative]: Auth[F, jwt.JwtAuth] =
    new Auth[F, jwt.JwtAuth] {
      override def auth(tokenKey: JwtAccessTokenKeyConfig): F[jwt.JwtAuth] =
        jwt.JwtAuth
          .hmac(tokenKey.secret.value, pdi.jwt.JwtAlgorithm.HS256)
          .pure
          .widen
    }
}
