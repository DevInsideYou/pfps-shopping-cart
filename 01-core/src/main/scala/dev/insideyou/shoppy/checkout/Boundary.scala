package dev.insideyou
package shoppy
package checkout

import scala.concurrent.duration._

import cats._
import cats.data.NonEmptyList
import cats.syntax.all._

trait Boundary[F[_]] {
  def process(userId: UserId, card: Card): F[ordering.OrderId]
}

object BoundaryImpl {
  def make[F[_]: MonadThrow: Background](
      gate: Gate[F]
  ): Boundary[F] =
    new Boundary[F] {
      override def process(userId: UserId, card: Card): F[ordering.OrderId] =
        gate
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
          pid <- gate.processPayment(Payment(userId, cartTotal.total, card))
          oid <- bgAction(pid, gate.createOrder(userId, pid, its, cartTotal.total))
          _   <- gate.clearCart(userId).attempt.void
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

            gate.logger
              .error(message)
              .productR(Background[F].schedule(bgAction(paymentId, fa), 1.hour))
        }
    }
}
