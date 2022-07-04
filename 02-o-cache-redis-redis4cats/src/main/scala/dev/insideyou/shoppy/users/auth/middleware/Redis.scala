package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands
import io.circe.parser.decode

import CirceCodecs._

object RedisImpl {
  def make[F[_]: Functor, Token: Show](
      redis: RedisCommands[F, String, String]
  ): Redis[F, Token] =
    new Redis[F, Token] {
      override def getUserFromCache(token: Token): F[Option[User]] =
        redis.get(token.show).map { optionOfString =>
          for {
            string <- optionOfString
            user   <- decode[User](string).toOption
          } yield user
        }
    }
}
