package dev.insideyou
package shoppy
package branding

import io.circe._
import io.circe.generic.semiauto._

object CirceCodecs {
  implicit lazy val brandIdEncoder: Encoder[BrandId] =
    BrandId.deriving

  implicit lazy val brandNameEncoder: Encoder[BrandName] =
    BrandName.deriving

  implicit lazy val brandEncoder: Encoder[Brand] =
    deriveEncoder
}
