package dev.insideyou
package shoppy
package branding

import io.circe._
import io.circe.magnolia.derivation.encoder.semiauto._
import io.circe.magnolia.derivation.decoder.semiauto._

object CirceCodecs {
  implicit lazy val brandIdCodec: Codec[BrandId] =
    Codec.from(BrandId.deriving, BrandId.deriving)

  implicit lazy val brandNameCodec: Codec[BrandName] =
    Codec.from(BrandName.deriving, BrandName.deriving)

  implicit lazy val brandCodec: Codec[Brand] =
    Codec.from(deriveMagnoliaDecoder, deriveMagnoliaEncoder)
}
