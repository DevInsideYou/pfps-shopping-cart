package dev.insideyou
package shoppy
package categories

import io.circe._
import io.circe.generic.semiauto._

object CirceCodecs {
  implicit lazy val categoryIdEncoder: Encoder[CategoryId] =
    CategoryId.deriving

  implicit lazy val categoryNameEncoder: Encoder[CategoryName] =
    CategoryName.deriving

  implicit lazy val categoryEncoder: Encoder[Category] =
    deriveEncoder
}
