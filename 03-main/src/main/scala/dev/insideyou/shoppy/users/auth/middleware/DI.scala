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
  ): Controller.Middleware[F, CommonUser] =
    ControllerImpl.make(
      boundary = BoundaryImpl.make(
        gate = Gate.make(
          hasConfig = HasConfigImpl.make,
          redis = RedisImpl.make(redis),
          reprMaker = ReprMakerImpl.make,
          tokens = TokensImpl.make
        )
      )
    )

  private implicit val ShowForJwtToken: Show[jwt.JwtToken] =
    Show.fromToString
}
