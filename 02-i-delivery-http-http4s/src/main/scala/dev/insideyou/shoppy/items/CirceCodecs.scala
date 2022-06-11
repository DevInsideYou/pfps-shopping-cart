package dev.insideyou
package shoppy
package items

import cats._
import io.circe._
import io.circe.generic.semiauto._
import squants.market._

import branding.CirceCodecs._
import categories.CirceCodecs._

object CirceCodecs {
  implicit lazy val itemIdCodec: Codec[ItemId] =
    Codec.from(ItemId.deriving, ItemId.deriving)

  implicit lazy val itemIdKeyEncoder: KeyEncoder[ItemId] =
    ItemId.deriving

  implicit lazy val itemIdKeyDecoder: KeyDecoder[ItemId] =
    ItemId.deriving

  implicit lazy val itemNameCodec: Codec[ItemName] =
    Codec.from(ItemName.deriving, ItemName.deriving)

  implicit lazy val itemDescriptionCodec: Codec[ItemDescription] =
    Codec.from(ItemDescription.deriving, ItemDescription.deriving)

  implicit lazy val itemCodec: Codec[Item] =
    Codec.from(deriveDecoder, deriveEncoder)

  implicit lazy val moneyCodec: Codec[Money] =
    Codec.from(
      Decoder[BigDecimal].map(USD.apply),
      Encoder[BigDecimal].contramap(_.amount)
    )

  implicit lazy val quantityIdCodec: Codec[Quantity] =
    Codec.from(Quantity.deriving, Quantity.deriving)

}
