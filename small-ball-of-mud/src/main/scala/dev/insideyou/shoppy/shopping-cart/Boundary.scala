package dev.insideyou
package shoppy
package shopping_cart

trait Boundary[F[_]] {
  def add(userId: UserId, itemId: items.ItemId, quantity: items.Quantity): F[Unit]
  def get(userId: UserId): F[CartTotal]
  def removeItem(userId: UserId, itemId: items.ItemId): F[Unit]
  def update(userId: UserId, cart: Cart): F[Unit]
}
