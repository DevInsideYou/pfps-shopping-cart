package dev

import scala.concurrent.duration.FiniteDuration

import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype

package object insideyou {
  @newtype
  final case class PaymentURI(value: NonEmptyString)

  @newtype
  final case class PaymentConfig(uri: PaymentURI)

  @newtype
  final case class TokenExpiration(value: FiniteDuration)
}
