package dev.insideyou
package shoppy
package checkout

import cats.data.NonEmptyList
import cats.effect._
import cats.syntax.all._
import retry.RetryPolicy
import skunk._
import skunk.implicits._
import squants.market.Money

object StoragePostgresImpl {
  def make[F[_]: MonadCancelThrow: Retry: GenUUID](
      postgres: Resource[F, Session[F]],
      policy: RetryPolicy[F]
  ): Storage[F] =
    new Storage[F] {
      override def createOrder(
          userId: UserId,
          paymentId: ordering.PaymentId,
          items: NonEmptyList[shopping_cart.CartItem],
          total: Money
      ): F[ordering.OrderId] =
        Retry[F]
          .retry(policy, Retriable.Orders)(doCreateOrder(userId, paymentId, items, total))
          .adaptError {
            case e => Error.Order(e.getMessage)
          }

      private def doCreateOrder(
          userId: UserId,
          paymentId: ordering.PaymentId,
          items: NonEmptyList[shopping_cart.CartItem],
          total: Money
      ): F[ordering.OrderId] =
        postgres.use { session =>
          session.prepare(ordering.StoragePostgresImpl.SQL.insertOrder).use { cmd =>
            ID.make[F, ordering.OrderId].flatMap { id =>
              val itMap = items.toList.map(x => x.item.uuid -> x.quantity).toMap
              val order = ordering.Order(id, paymentId, itMap, total)

              cmd.execute(userId ~ order).as(id)
            }
          }
        }
    }
}
