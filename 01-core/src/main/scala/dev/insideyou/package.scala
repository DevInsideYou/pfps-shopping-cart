package dev

import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype

package object insideyou {
  @newtype
  final case class PaymentURI(value: NonEmptyString)

  @newtype
  final case class PaymentConfig(uri: PaymentURI)
}
