package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands

object RedisImpl {
  def make[F[_]: NonEmptyParallel: Functor](
      redis: RedisCommands[F, String, String]
  ): Redis[F] =
    new Redis[F] {
      override def cacheUserInRedis(
          userRepr: UserRepr,
          userName: UserName,
          token: JwtToken,
          expiresIn: TokenExpiration
      ): F[Unit] =
        (
          redis.setEx(token.show, userRepr.show, expiresIn.value),
          redis.setEx(userName.show, token.show, expiresIn.value)
        ).parTupled.void

      override def cacheUserWithPasswordInRedis(
          userWithPassword: UserWithPassword,
          expiresIn: TokenExpiration
      )(
          token: JwtToken
      ): F[Unit] =
        redis.setEx(userWithPassword.name.show, token.show, expiresIn.value)

      override def getTokenFromRedis(username: UserName): F[Option[JwtToken]] =
        redis.get(username.show).nested.map(JwtToken.apply).value

      override def deleteUserInRedis(username: UserName, token: JwtToken): F[Unit] =
        (
          redis.del(token.show),
          redis.del(username.show)
        ).parTupled.void
    }
}
