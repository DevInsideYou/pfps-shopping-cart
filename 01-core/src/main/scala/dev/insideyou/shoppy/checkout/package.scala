package dev.insideyou
package shoppy

import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype

package object checkout {
  @newtype
  final case class PaymentURI(value: NonEmptyString)

  @newtype
  final case class PaymentConfig(uri: PaymentURI)
}
