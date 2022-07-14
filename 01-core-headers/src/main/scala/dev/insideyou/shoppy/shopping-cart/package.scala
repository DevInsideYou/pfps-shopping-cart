package dev.insideyou
package shoppy

import cats._
import derevo.cats._
import derevo.derive
import dev.insideyou.{ shoppy => domain }
import io.estatico.newtype.macros.newtype
import squants.market._
import scala.concurrent.duration._

package object shopping_cart {
  @derive(eqv, show)
  @newtype
  final case class Cart(items: Map[domain.items.ItemId, domain.items.Quantity])

  @newtype
  final case class ShoppingCartExpiration(value: FiniteDuration)

  implicit lazy val moneyEq: Eq[Money] =
    Eq.fromUniversalEquals

  implicit lazy val moneyShow: Show[Money] =
    Show.fromToString
}
