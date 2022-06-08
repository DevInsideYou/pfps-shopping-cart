package dev.insideyou
package shoppy
package ordering

import io.circe._
import io.circe.generic.semiauto._

import items.CirceCodecs._

object CirceCodecs {
  implicit lazy val orderIdEncoder: Encoder[OrderId] =
    OrderId.deriving

  implicit lazy val paymentIdEncoder: Encoder[PaymentId] =
    PaymentId.deriving

  implicit lazy val orderEncoder: Encoder[Order] =
    deriveEncoder
}
