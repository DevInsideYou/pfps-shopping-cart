package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats.Applicative
import cats.syntax.all._

import io.circe.parser.decode

import CirceCodecs._

object ReprMakerImpl {
  def make[F[_]: Applicative]: ReprMaker[F] =
    new ReprMaker[F] {
      override def convertToCommonUser(userString: String): F[Option[CommonUser]] =
        decode[User](userString).toOption.map(CommonUser.apply).pure
    }
}
