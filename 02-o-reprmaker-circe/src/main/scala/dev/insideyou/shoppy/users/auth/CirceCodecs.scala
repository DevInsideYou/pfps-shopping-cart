package dev.insideyou
package shoppy
package users
package auth

import io.circe._

object CirceCodecs {
  @scala.annotation.nowarn("cat=unused")
  implicit lazy val codecForUser: Codec[User] = {
    import derevo.circe.magnolia._

    implicit val codecForUserId: Codec[UserId] =
      Codec.from(UserId.deriving, UserId.deriving)

    implicit val codecForUserName: Codec[UserName] =
      Codec.from(UserName.deriving, UserName.deriving)

    Codec.from(decoder.instance, encoder.instance)
  }
}
