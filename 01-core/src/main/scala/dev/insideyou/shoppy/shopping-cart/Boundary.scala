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
  def make[F[_]: Monad](dependencies: Dependencies[F]): Boundary[F] =
    new Boundary[F] {
      override def add(userId: UserId, itemId: items.ItemId, quantity: items.Quantity): F[Unit] =
        for {
          config <- dependencies.config
          _      <- dependencies.setItem(userId, itemId, quantity, config.exp)
        } yield ()

      override def get(userId: UserId): F[CartTotal] =
        for {
          cart      <- dependencies.getAllUserItems(userId).map(_.toList)
          cartItems <- cart.traverseFilter(Function.tupled(findItems))
        } yield CartTotal(cartItems, cartItems.foldMap(_.subTotal))

      private def findItems(itemId: items.ItemId, quantity: items.Quantity): F[Option[CartItem]] =
        dependencies.findItemsById(itemId).map(_.map(_.cart(quantity)))

      override def removeItem(userId: UserId, itemId: items.ItemId): F[Unit] =
        dependencies.removeItem(userId, itemId)

      override def update(userId: UserId, cart: Cart): F[Unit] =
        for {
          config  <- dependencies.config
          itemIds <- dependencies.getAllUserItems(userId).map(_.toList._1F)
          _       <- itemIds.traverse(setItem(userId, cart))
          _       <- dependencies.expire(userId, config.exp)
        } yield ()

      private def setItem(userId: UserId, cart: Cart)(itemId: items.ItemId): F[Unit] =
        cart.items.get(itemId).traverse_ { quantity =>
          dependencies.setItem(userId, itemId, quantity)
        }
    }

  private implicit lazy val moneyMonoid: Monoid[Money] =
    new Monoid[Money] {
      def empty: Money                       = USD(0)
      def combine(x: Money, y: Money): Money = x + y
    }

  private implicit final class CartItemOps(private val self: items.Item) extends AnyVal {
    def cart(quantity: items.Quantity): CartItem =
      CartItem(self, quantity)
  }

  trait Dependencies[F[_]] extends HasConfig[F, Config] with Persistence[F] with Redis[F]

  def make[F[_]: Monad](
      hasConfig: HasConfig[F, Config],
      persistence: Persistence[F],
      redis: Redis[F]
  ): Boundary[F] =
    make {
      new Dependencies[F] {
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

}
