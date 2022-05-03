package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands
import io.circe._
import io.circe.syntax._

// TODO remove the dependency on JSON

object RedisImpl {
  def make[F[_]: NonEmptyParallel: Functor](
      redis: RedisCommands[F, String, String]
  ): Redis[F] =
    new Redis[F] {
      override def setUserInRedis(
          user: User,
          token: JwtToken,
          expiresIn: TokenExpiration
      ): F[Unit] =
        redis.setEx(token.value, user.asJson.noSpaces, expiresIn.value)

      override def setUserWithPasswordInRedis(
          user: UserWithPassword,
          expiresIn: TokenExpiration
      )(
          token: JwtToken
      ): F[Unit] =
        redis.setEx(user.name.show, token.value, expiresIn.value)

      override def getTokenFromRedis(username: UserName): F[Option[JwtToken]] =
        redis.get(username.show).nested.map(JwtToken.apply).value

      override def deleteUserInRedis(username: UserName, token: JwtToken): F[Unit] =
        (redis.del(token.show), redis.del(username.show)).parTupled.void
    }

  @scala.annotation.nowarn("cat=unused")
  private implicit lazy val a: Encoder[User] = {
    implicit lazy val b: Encoder[UserId]   = UserId.deriving
    implicit lazy val c: Encoder[UserName] = UserName.deriving

    derevo.circe.magnolia.encoder.instance
  }
}
