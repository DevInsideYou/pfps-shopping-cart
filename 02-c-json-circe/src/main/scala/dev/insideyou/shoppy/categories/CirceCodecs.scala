package dev.insideyou
package shoppy
package categories

import io.circe._
import derevo.circe.magnolia._

object CirceCodecs {
  implicit lazy val categoryIdCodec: Codec[CategoryId] =
    Codec.from(CategoryId.deriving, CategoryId.deriving)

  implicit lazy val categoryNameCodec: Codec[CategoryName] =
    Codec.from(CategoryName.deriving, CategoryName.deriving)

  implicit lazy val categoryCodec: Codec[Category] =
    Codec.from(decoder.instance, encoder.instance)
}
