package dev.insideyou
package shoppy
package users
package auth

import scala.concurrent.duration._

import cats.effect._
import cats.syntax.all._
import ciris._
import ciris.refined._
import derevo.cats.show
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype

object HasConfigImpl {
  def make[F[_]: Async]: HasConfig[F] =
    new HasConfig[F] {
      override def config: F[Config] =
        (
          ConfigValue.default(TokenExpiration(30.minutes)),
          env("SC_ACCESS_TOKEN_SECRET_KEY").as[LocalJwtAccessTokenKeyConfig].secret,
          env("SC_PASSWORD_SALT").as[LocalPasswordSalt].secret
        ).parMapN { (tokenExpiration, jwt, passwordSalt) =>
            Config(
              tokenExpiration = tokenExpiration,
              jwtAccessTokenKeyConfig = JwtAccessTokenKeyConfig(jwt.value.secret),
              passwordSalt = PasswordSalt(passwordSalt.value.secret)
            )
          }
          .load[F]

    }

  @derive(configDecoder, show)
  @newtype
  private final case class LocalJwtAccessTokenKeyConfig(secret: NonEmptyString)

  @derive(configDecoder, show)
  @newtype
  private final case class LocalPasswordSalt(secret: NonEmptyString)
}
