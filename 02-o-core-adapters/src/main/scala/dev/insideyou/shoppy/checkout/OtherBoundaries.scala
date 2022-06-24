package dev.insideyou
package shoppy
package checkout

object OtherBoundariesImpl {
  def make[F[_]](shoppingCartBoundary: shopping_cart.Boundary[F]): OtherBoundaries[F] =
    new OtherBoundaries[F] {
      override def getCartTotal(userId: UserId): F[shopping_cart.CartTotal] =
        shoppingCartBoundary.get(userId)
    }
}
