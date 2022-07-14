package dev.insideyou
package shoppy
package checkout

import cats.data.NonEmptyList
import squants.market.Money

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
