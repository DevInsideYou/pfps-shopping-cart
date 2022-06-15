package dev.insideyou
package shoppy
package shopping_cart

import cats._
import cats.syntax.all._
import squants.market._

trait Boundary[F[_]] {
  def add(userId: UserId, itemId: items.ItemId, quantity: items.Quantity): F[Unit]
  def get(userId: UserId): F[CartTotal]
  def removeItem(userId: UserId, itemId: items.ItemId): F[Unit]
  def update(userId: UserId, cart: Cart): F[Unit]
}

object BoundaryImpl {
  def make[F[_]: Monad](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      override def add(userId: UserId, itemId: items.ItemId, quantity: items.Quantity): F[Unit] =
        for {
          config <- gate.config
          _      <- gate.setItem(userId, itemId, quantity, config.exp)
        } yield ()

      override def get(userId: UserId): F[CartTotal] =
        for {
          cart      <- gate.getAllUserItems(userId).map(_.toList)
          cartItems <- cart.traverseFilter(Function.tupled(findItems))
        } yield CartTotal(cartItems, cartItems.foldMap(_.subTotal))

      private def findItems(itemId: items.ItemId, quantity: items.Quantity): F[Option[CartItem]] =
        gate.findItemsById(itemId).map(_.map(_.cart(quantity)))

      override def removeItem(userId: UserId, itemId: items.ItemId): F[Unit] =
        gate.removeItem(userId, itemId)

      override def update(userId: UserId, cart: Cart): F[Unit] =
        for {
          config  <- gate.config
          itemIds <- gate.getAllUserItems(userId).map(_.toList._1F)
          _       <- itemIds.traverse(setItem(userId, cart))
          _       <- gate.expire(userId, config.exp)
        } yield ()

      private def setItem(userId: UserId, cart: Cart)(itemId: items.ItemId): F[Unit] =
        cart.items.get(itemId).traverse_ { quantity =>
          gate.setItem(userId, itemId, quantity)
        }
    }

  private implicit val moneyMonoid: Monoid[Money] =
    new Monoid[Money] {
      def empty: Money                       = USD(0)
      def combine(x: Money, y: Money): Money = x + y
    }

  private implicit final class CartItemOps(private val self: items.Item) extends AnyVal {
    def cart(quantity: items.Quantity): CartItem =
      CartItem(self, quantity)
  }
}
