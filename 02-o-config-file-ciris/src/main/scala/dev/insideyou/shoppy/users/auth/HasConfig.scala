package dev.insideyou
package shoppy
package users
package auth

import scala.concurrent.duration._

import cats.effect._
import cats.syntax.all._
import ciris._
import ciris.refined._
import eu.timepit.refined.types.string.NonEmptyString

object HasConfigImpl {
  def make[F[_]: Async]: HasConfig[F] =
    new HasConfig[F] {
      override def config: F[Config] =
        (
          ConfigValue.default(TokenExpiration(30.minutes)),
          env("SC_ACCESS_TOKEN_SECRET_KEY").as[JwtAccessTokenKeyConfig],
          env("SC_PASSWORD_SALT").as[PasswordSalt]
        ).parMapN(Config).load[F]
    }

  private implicit lazy val a: ConfigDecoder[String, JwtAccessTokenKeyConfig] =
    ConfigDecoder[String, NonEmptyString].map(JwtAccessTokenKeyConfig.apply)

  private implicit lazy val b: ConfigDecoder[String, PasswordSalt] =
    ConfigDecoder[String, NonEmptyString].map(PasswordSalt.apply)
}
