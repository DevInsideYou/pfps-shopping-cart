package dev.insideyou
package shoppy
package checkout

import cats._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands

object RedisImpl {
  def make[F[_]: Functor](
      redis: RedisCommands[F, String, String]
  ): Redis[F] =
    new Redis[F] {
      override def clearCart(userId: UserId): F[Unit] =
        redis.del(userId.show).void
    }
}
