package dev.insideyou
package shoppy
package categories

import io.circe._
import io.circe.magnolia.derivation.encoder.semiauto._
import io.circe.magnolia.derivation.decoder.semiauto._

object CirceCodecs {
  implicit lazy val categoryIdCodec: Codec[CategoryId] =
    Codec.from(CategoryId.deriving, CategoryId.deriving)

  implicit lazy val categoryNameCodec: Codec[CategoryName] =
    Codec.from(CategoryName.deriving, CategoryName.deriving)

  implicit lazy val categoryCodec: Codec[Category] =
    Codec.from(deriveMagnoliaDecoder, deriveMagnoliaEncoder)
}
