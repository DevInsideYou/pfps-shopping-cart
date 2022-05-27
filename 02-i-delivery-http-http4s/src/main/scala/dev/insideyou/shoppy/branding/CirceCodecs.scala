package dev.insideyou
package shoppy
package branding

import io.circe._

object CirceCodecs {
  implicit lazy val brandIdEncoder: Encoder[BrandId] =
    BrandId.deriving

  implicit lazy val brandNameEncoder: Encoder[BrandName] =
    BrandName.deriving
}
