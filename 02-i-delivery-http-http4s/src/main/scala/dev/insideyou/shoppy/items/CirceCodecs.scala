package dev.insideyou
package shoppy
package items

import io.circe._
import io.circe.generic.semiauto._
import squants.market._

import branding.CirceCodecs._
import categories.CirceCodecs._

object CirceCodecs {
  implicit lazy val itemIdEncoder: Encoder[ItemId] =
    ItemId.deriving

  implicit lazy val itemIdKeyEncoder: KeyEncoder[ItemId] =
    ItemId.deriving

  implicit lazy val itemNameEncoder: Encoder[ItemName] =
    ItemName.deriving

  implicit lazy val itemDescriptionEncoder: Encoder[ItemDescription] =
    ItemDescription.deriving

  implicit lazy val itemEncoder: Encoder[Item] =
    deriveEncoder

  implicit lazy val moneyCodec: Codec[Money] =
    Codec.from(
      Decoder[BigDecimal].map(USD.apply),
      Encoder[BigDecimal].contramap(_.amount)
    )

  implicit lazy val quantityIdEncoder: Encoder[Quantity] =
    Quantity.deriving
}
