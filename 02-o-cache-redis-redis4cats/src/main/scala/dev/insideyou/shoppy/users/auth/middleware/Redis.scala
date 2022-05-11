package dev.insideyou
package shoppy
package users
package auth
package middleware

import dev.profunktor.redis4cats.RedisCommands

object RedisImpl {
  def make[F[_]](
      redis: RedisCommands[F, String, String]
  ): Redis[F] =
    new Redis[F] {
      override def getUserStringFromCache(token: JwtToken): F[Option[String]] =
        redis.get(token.value)
    }
}
