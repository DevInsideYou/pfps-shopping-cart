package dev.insideyou
package shoppy
package shopping_cart

import derevo.cats._
import derevo.derive
import squants.market._

@derive(eqv, show)
final case class CartItem(item: items.Item, quantity: items.Quantity) {
  def subTotal: Money = USD(item.price.amount * quantity.value)
}
