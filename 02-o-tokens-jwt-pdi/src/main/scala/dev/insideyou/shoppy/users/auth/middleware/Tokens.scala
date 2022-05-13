package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.syntax.all._
import dev.insideyou.shoppy.users.auth.JwtAccessTokenKeyConfig
import dev.profunktor.auth.jwt
import pdi.jwt.JwtAlgorithm

object TokensImpl {
  def make[F[_]: Applicative]: Tokens[F, jwt.JwtAuth] =
    new Tokens[F, jwt.JwtAuth] {
      override def auth(tokenKey: JwtAccessTokenKeyConfig): F[jwt.JwtAuth] =
        jwt.JwtAuth
          .hmac(tokenKey.secret.value, JwtAlgorithm.HS256)
          .pure
          .widen
    }
}
