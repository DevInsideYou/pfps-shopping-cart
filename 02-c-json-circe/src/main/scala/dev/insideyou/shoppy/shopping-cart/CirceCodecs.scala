package dev.insideyou
package shoppy
package shopping_cart

import io.circe._
import derevo.circe.magnolia._

import items.CirceCodecs._

object CirceCodecs {
  implicit lazy val cartItemCodec: Codec[CartItem] =
    Codec.from(decoder.instance, encoder.instance)

  implicit lazy val cartTotalCodec: Codec[CartTotal] =
    Codec.from(decoder.instance, encoder.instance)

  implicit lazy val jsonCodec: Codec[Cart] =
    Codec.from(Decoder.forProduct1("items")(Cart.apply), Encoder.forProduct1("items")(_.items))
}
