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
        gate.addItems(userId, itemId, quantity)

      override def get(userId: UserId): F[CartTotal] =
        gate.getAllUserItems(userId).flatMap {
          _.toList
            .traverseFilter {
              case (id, qt) =>
                for {
                  // id <- ID.read[F, ItemId](k)
                  // qt <- MonadThrow[F].catchNonFatal(Quantity(v.toInt))
                  rs <- gate.findItemsById(id).map(_.map(_.cart(qt)))
                } yield rs
            }
            .map(items => CartTotal(items, items.foldMap(_.subTotal)))
        }

      override def removeItem(userId: UserId, itemId: items.ItemId): F[Unit] =
        gate.removeItem(userId, itemId)

      override def update(userId: UserId, cart: Cart): F[Unit] =
        ???
    }

  private implicit val moneyMonoid: Monoid[Money] =
    new Monoid[Money] {
      def empty: Money                       = USD(0)
      def combine(x: Money, y: Money): Money = x + y
    }
}

trait Gate[F[_]] {
  def addItems(userId: UserId, itemId: items.ItemId, quantity: items.Quantity): F[Unit]
  def removeItem(userId: UserId, itemId: items.ItemId): F[Unit]
  def getAllUserItems(userId: UserId): F[Map[items.ItemId, items.Quantity]]
  def findItemsById(itemId: items.ItemId): F[Option[items.Item]]
}
