package dev.insideyou
package shoppy
package checkout

import scala.concurrent.duration._
import cats._
import cats.data.NonEmptyList
import cats.syntax.all._
import squants.market.Money

object BoundaryImpl {
  def make[F[_]: MonadThrow: Background](
      dependencies: Dependencies[F]
  ): Boundary[F] =
    new Boundary[F] {
      override def process(userId: UserId, card: Card): F[ordering.OrderId] =
        dependencies
          .getCartTotal(userId)
          .flatMap(processCartTotal(userId, card))

      private def processCartTotal(
          userId: UserId,
          card: Card
      )(
          cartTotal: shopping_cart.CartTotal
      ): F[ordering.OrderId] =
        for {
          its <- ensureNonEmpty(cartTotal.items)
          pid <- dependencies.processPayment(Payment(userId, cartTotal.total, card))
          oid <- bgAction(pid, dependencies.createOrder(userId, pid, its, cartTotal.total))
          _   <- dependencies.clearCart(userId).attempt.void
        } yield oid

      private def ensureNonEmpty[A](xs: List[A]): F[NonEmptyList[A]] =
        MonadThrow[F].fromOption(NonEmptyList.fromList(xs), EmptyCartError)

      private def bgAction(
          paymentId: ordering.PaymentId,
          fa: F[ordering.OrderId]
      ): F[ordering.OrderId] =
        fa.onError {
          case _ =>
            lazy val message =
              s"Failed to create order for Payment: ${paymentId.show}. Rescheduling as a background action"

            dependencies.logger
              .error(message)
              .productR(Background[F].schedule(bgAction(paymentId, fa), 1.hour))
        }
    }

  trait Dependencies[F[_]]
      extends HasLogger[F]
      with PaymentClient[F]
      with Persistence[F]
      with Redis[F]
      with OtherBoundaries[F]

  def make[F[_]: MonadThrow: Background](
      hasLogger: HasLogger[F],
      paymentClient: PaymentClient[F],
      persistence: Persistence[F],
      redis: Redis[F],
      otherBoundaries: OtherBoundaries[F]
  ): Boundary[F] =
    make {
      new Dependencies[F] {
        override def logger: Logger[F] =
          hasLogger.logger

        override def processPayment(payment: Payment): F[ordering.PaymentId] =
          paymentClient.processPayment(payment)

        override def createOrder(
            userId: UserId,
            paymentId: ordering.PaymentId,
            items: NonEmptyList[shopping_cart.CartItem],
            total: Money
        ): F[ordering.OrderId] =
          persistence.createOrder(userId, paymentId, items, total)

        override def clearCart(userId: UserId): F[Unit] =
          redis.clearCart(userId)

        override def getCartTotal(userId: UserId): F[shopping_cart.CartTotal] =
          otherBoundaries.getCartTotal(userId)
      }
    }
}
