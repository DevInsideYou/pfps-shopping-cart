package dev.insideyou
package shoppy
package shopping_cart

import cats._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands

object RedisImpl {
  def make[F[_]: NonEmptyParallel: MonadThrow: GenUUID](
      redis: RedisCommands[F, String, String]
  ): Redis[F] =
    new Redis[F] {
      override def setItem(
          userId: UserId,
          itemId: items.ItemId,
          quantity: items.Quantity,
          exp: ShoppingCartExpiration
      ): F[Unit] =
        (
          redis.hSet(userId.show, itemId.show, quantity.show),
          redis.expire(userId.show, exp.value)
        ).parTupled.void

      override def setItem(
          userId: UserId,
          itemId: items.ItemId,
          quantity: items.Quantity
      ): F[Unit] =
        redis.hSet(userId.show, itemId.show, quantity.show).void

      override def expire(userId: UserId, exp: ShoppingCartExpiration): F[Unit] =
        redis.expire(userId.show, exp.value).void

      override def removeItem(userId: UserId, itemId: items.ItemId): F[Unit] =
        redis.hDel(userId.show, itemId.show).void

      override def getAllUserItems(userId: UserId): F[Map[items.ItemId, items.Quantity]] =
        for {
          raw  <- redis.hGetAll(userId.show).map(_.toList)
          cart <- raw.traverse(Function.tupled(transform))
        } yield cart.toMap

      private def transform(k: String, v: String): F[(items.ItemId, items.Quantity)] =
        (
          ID.read[F, items.ItemId](k),
          MonadThrow[F].catchNonFatal(items.Quantity(v.toInt))
        ).parTupled
    }
}
