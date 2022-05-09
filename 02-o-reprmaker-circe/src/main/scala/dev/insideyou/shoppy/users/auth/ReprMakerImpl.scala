package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._
import io.circe.syntax._

import CirceCodecs._

object ReprMakerImpl {
  def make[F[_]: Applicative]: ReprMaker[F] =
    new ReprMaker[F] {
      override def makeUserRepr(user: User): F[UserRepr] =
        UserRepr(user.asJson.noSpaces).pure
    }
}
