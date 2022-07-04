package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands
import io.circe.syntax._

import CirceCodecs._

object RedisImpl {
  def make[F[_]: NonEmptyParallel: Functor, Token: Show](
      redis: RedisCommands[F, String, String],
      stringToToken: String => Token
  ): Redis[F, Token] =
    new Redis[F, Token] {
      override def cacheUserInRedis(
          user: User,
          userName: UserName,
          token: Token,
          expiresIn: TokenExpiration
      ): F[Unit] =
        (
          redis.setEx(token.show, user.asJson.noSpaces.show, expiresIn.value),
          redis.setEx(userName.show, token.show, expiresIn.value)
        ).parTupled.void

      override def cacheUserWithPasswordInRedis(
          userWithPassword: UserWithPassword,
          expiresIn: TokenExpiration
      )(
          token: Token
      ): F[Unit] =
        redis.setEx(userWithPassword.name.show, token.show, expiresIn.value)

      override def getTokenFromRedis(username: UserName): F[Option[Token]] =
        redis.get(username.show).nested.map(stringToToken).value

      override def deleteUserInRedis(username: UserName, token: Token): F[Unit] =
        (
          redis.del(token.show),
          redis.del(username.show)
        ).parTupled.void
    }
}
