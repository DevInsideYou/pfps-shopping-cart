package dev.insideyou
package shoppy
package shopping_cart

import derevo.cats._
import derevo.derive
import squants.market._

@derive(eqv, show)
final case class CartTotal(items: List[CartItem], total: Money)
