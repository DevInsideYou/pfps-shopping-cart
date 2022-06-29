package dev.insideyou
package shoppy
package checkout

import cats.data.NonEmptyList
import squants.market.Money

trait Gate[F[_]]
    extends HasLogger[F]
    with PaymentClient[F]
    with Persistence[F]
    with Redis[F]
    with OtherBoundaries[F]

object Gate {
  def make[F[_]](
      hasLogger: HasLogger[F],
      paymentClient: PaymentClient[F],
      persistence: Persistence[F],
      redis: Redis[F],
      otherBoundaries: OtherBoundaries[F]
  ): Gate[F] =
    new Gate[F] {
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

trait PaymentClient[F[_]] {
  def processPayment(payment: Payment): F[ordering.PaymentId]
}

trait Persistence[F[_]] {
  def createOrder(
      userId: UserId,
      paymentId: ordering.PaymentId,
      items: NonEmptyList[shopping_cart.CartItem],
      total: Money
  ): F[ordering.OrderId]
}

trait OtherBoundaries[F[_]] {
  def getCartTotal(userId: UserId): F[shopping_cart.CartTotal]
}

trait Redis[F[_]] {
  def clearCart(userId: UserId): F[Unit]
}
