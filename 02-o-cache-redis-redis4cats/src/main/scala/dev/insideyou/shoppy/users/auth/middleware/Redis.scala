package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands

object RedisImpl {
  def make[F[_], Token: Show](
      redis: RedisCommands[F, String, String]
  ): Redis[F, Token] =
    new Redis[F, Token] {
      override def getUserStringFromCache(token: Token): F[Option[String]] =
        redis.get(token.show)
    }
}
