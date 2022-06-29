package dev.insideyou
package shoppy
package shopping_cart

trait Gate[F[_]] extends HasConfig[F, Config] with Persistence[F] with Redis[F]

object Gate {
  def make[F[_]](
      hasConfig: HasConfig[F, Config],
      persistence: Persistence[F],
      redis: Redis[F]
  ): Gate[F] =
    new Gate[F] {
      override def config: F[Config] =
        hasConfig.config

      override def findItemsById(itemId: items.ItemId): F[Option[items.Item]] =
        persistence.findItemsById(itemId)

      override def setItem(
          userId: UserId,
          itemId: items.ItemId,
          quantity: items.Quantity,
          exp: ShoppingCartExpiration
      ): F[Unit] =
        redis.setItem(userId, itemId, quantity, exp)

      override def setItem(
          userId: UserId,
          itemId: items.ItemId,
          quantity: items.Quantity
      ): F[Unit] =
        redis.setItem(userId, itemId, quantity)

      override def expire(userId: UserId, exp: ShoppingCartExpiration): F[Unit] =
        redis.expire(userId, exp)

      override def removeItem(userId: UserId, itemId: items.ItemId): F[Unit] =
        redis.removeItem(userId, itemId)

      override def getAllUserItems(userId: UserId): F[Map[items.ItemId, items.Quantity]] =
        redis.getAllUserItems(userId)
    }
}

trait Persistence[F[_]] {
  def findItemsById(itemId: items.ItemId): F[Option[items.Item]]
}

trait Redis[F[_]] {
  def setItem(
      userId: UserId,
      itemId: items.ItemId,
      quantity: items.Quantity,
      exp: ShoppingCartExpiration
  ): F[Unit]

  def setItem(
      userId: UserId,
      itemId: items.ItemId,
      quantity: items.Quantity
  ): F[Unit]

  def expire(userId: UserId, exp: ShoppingCartExpiration): F[Unit]
  def removeItem(userId: UserId, itemId: items.ItemId): F[Unit]
  def getAllUserItems(userId: UserId): F[Map[items.ItemId, items.Quantity]]
}
