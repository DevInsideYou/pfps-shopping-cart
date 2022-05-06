package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._
import eu.timepit.refined.types.string
import io.circe._
import io.circe.syntax._

object ReprMakerImpl {
  def make[F[_]: Applicative]: ReprMaker[F] =
    new ReprMaker[F] {
      override def makeUserRepr(user: User): F[UserRepr] =
        UserRepr(string.NonEmptyString.unsafeFrom(user.asJson.noSpaces)).pure[F]
    }

  @scala.annotation.nowarn("cat=unused")
  private implicit lazy val a: Encoder[User] = {
    implicit lazy val b: Encoder[UserId]   = UserId.deriving
    implicit lazy val c: Encoder[UserName] = UserName.deriving

    derevo.circe.magnolia.encoder.instance
  }
}
