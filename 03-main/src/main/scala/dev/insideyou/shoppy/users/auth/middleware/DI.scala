package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.effect._
import dev.profunktor.auth.jwt
import dev.profunktor.redis4cats.RedisCommands

object DI {
  def make[F[_]: Async](
      redis: RedisCommands[F, String, String]
  ): Middleware[F, CommonUser] =
    MiddlewareImpl.make(
      boundary = BoundaryImpl.make(
        hasConfig = HasConfigImpl.make,
        redis = RedisImpl.make(redis),
        _auth = AuthImpl.make
      )
    )

  private implicit lazy val ShowForJwtToken: Show[jwt.JwtToken] =
    Show.fromToString
}
