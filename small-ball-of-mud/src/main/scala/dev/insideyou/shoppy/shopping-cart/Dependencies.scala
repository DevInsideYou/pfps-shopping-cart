package dev.insideyou
package shoppy
package shopping_cart

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
