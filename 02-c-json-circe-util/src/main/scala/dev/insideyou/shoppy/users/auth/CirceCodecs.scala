package dev.insideyou
package shoppy
package users
package auth

import io.circe._
import derevo.circe.magnolia._

object CirceCodecs {
  implicit lazy val userIdCodec: Codec[UserId] =
    Codec.from(UserId.deriving, UserId.deriving)

  implicit lazy val userNameCodec: Codec[UserName] =
    Codec.from(UserName.deriving, UserName.deriving)

  implicit lazy val userCodec: Codec[User] =
    Codec.from(decoder.instance, encoder.instance)
}
