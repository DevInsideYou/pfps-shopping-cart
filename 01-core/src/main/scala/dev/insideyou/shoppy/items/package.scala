package dev.insideyou
package shoppy

import java.util.UUID

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype

package object items {
  @derive(eqv, show, uuid)
  @newtype
  final case class ItemId(value: UUID)

  @derive(eqv, show)
  @newtype
  final case class ItemName(value: String)

  @derive(eqv, show)
  @newtype
  final case class ItemDescription(value: String)
  @derive(eqv, show)
  @newtype
  final case class Quantity(value: Int)
}
