package dev.insideyou
package shoppy
package items

import cats._
import derevo.cats._
import derevo.derive
import squants.market._

@derive(eqv, show)
final case class Item(
    uuid: ItemId,
    name: ItemName,
    description: ItemDescription,
    price: Money,
    brand: branding.Brand,
    category: categories.Category
) {
  def cart(q: Quantity): CartItem =
    CartItem(this, q)
}

object Item {
  implicit val currencyEq: Eq[Currency] =
    Eq.and(Eq.and(Eq.by(_.code), Eq.by(_.symbol)), Eq.by(_.name))

  implicit val moneyEq: Eq[Money] = Eq.and(Eq.by(_.amount), Eq.by(_.currency))

  implicit val moneyShow: Show[Money] = Show.fromToString
}

@derive(eqv, show)
final case class CartItem(item: Item, quantity: Quantity) {
  def subTotal: Money = USD(item.price.amount * quantity.value)
}
