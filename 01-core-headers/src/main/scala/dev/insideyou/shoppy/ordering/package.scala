package dev.insideyou
package shoppy

import java.util.UUID

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype

package object ordering {
  @derive(eqv, show, uuid)
  @newtype
  final case class OrderId(value: UUID)

  @derive(eqv, show, uuid)
  @newtype
  final case class PaymentId(value: UUID)
}
