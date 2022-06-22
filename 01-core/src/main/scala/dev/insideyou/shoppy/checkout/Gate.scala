package dev.insideyou
package shoppy
package checkout

import cats.data.NonEmptyList
import squants.market.Money

trait Gate[F[_]] extends HasLogger[F] {
  def getCartTotal(userId: UserId): F[shopping_cart.CartTotal]
  def processPayment(in: Payment): F[ordering.PaymentId]

  def createOrder(
      userId: UserId,
      paymentId: ordering.PaymentId,
      items: NonEmptyList[shopping_cart.CartItem],
      total: Money
  ): F[ordering.OrderId]

  def clearCart(userId: UserId): F[Unit]
}
