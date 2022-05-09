package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats.effect._
import dev.profunktor.redis4cats.RedisCommands
import pdi.jwt.JwtClaim

object DI {
  def make[F[_]: Async](
      redis: RedisCommands[F, String, String]
  ): Boundary[F, CommonUser, JwtClaim] =
    BoundaryImpl.make(
      gate = Gate.make(
        redis = RedisImpl.make(redis),
        reprMaker = ReprMakerImpl.make
      )
    )
}
