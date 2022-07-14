package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import io.circe.Decoder

object CirceCodecs {
  implicit lazy val jsonDecoder: Decoder[ClaimContent] =
    Decoder.forProduct1("uuid")(ClaimContent.apply)
}
