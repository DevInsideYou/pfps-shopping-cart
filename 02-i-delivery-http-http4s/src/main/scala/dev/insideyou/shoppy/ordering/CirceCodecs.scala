package dev.insideyou
package shoppy
package ordering

import io.circe._
import io.circe.magnolia.derivation.encoder.semiauto._

import items.CirceCodecs._

object CirceCodecs {
  implicit lazy val orderIdEncoder: Encoder[OrderId] =
    OrderId.deriving

  implicit lazy val paymentIdEncoder: Encoder[PaymentId] =
    PaymentId.deriving

  implicit lazy val orderEncoder: Encoder[Order] =
    deriveMagnoliaEncoder
}
