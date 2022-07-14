package dev.insideyou
package shoppy
package ordering

import io.circe._
import derevo.circe.magnolia._

import items.CirceCodecs._

object CirceCodecs {
  implicit lazy val orderIdCodec: Codec[OrderId] =
    Codec.from(OrderId.deriving, OrderId.deriving)

  implicit lazy val paymentIdCodec: Codec[PaymentId] =
    Codec.from(PaymentId.deriving, PaymentId.deriving)

  implicit lazy val orderCodec: Codec[Order] =
    Codec.from(decoder.instance, encoder.instance)
}
