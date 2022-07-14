package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats.effect._
import cats.syntax.all._
import ciris._
import ciris.refined._
import eu.timepit.refined.types.string.NonEmptyString

object HasConfigImpl {
  def make[F[_]: Async]: HasConfig[F, Config] =
    new HasConfig[F, Config] {
      override def config: F[Config] =
        (
          env("SC_JWT_SECRET_KEY").as[JwtSecretKeyConfig].secret,
          env("SC_ADMIN_USER_TOKEN").as[AdminUserTokenConfig].secret
        ).parMapN { (a, b) =>
            Config(a.value, b.value)
          }
          .load[F]
    }

  private implicit lazy val a: ConfigDecoder[String, JwtSecretKeyConfig] =
    ConfigDecoder[String, NonEmptyString].map(JwtSecretKeyConfig.apply)

  private implicit lazy val b: ConfigDecoder[String, AdminUserTokenConfig] =
    ConfigDecoder[String, NonEmptyString].map(AdminUserTokenConfig.apply)
}
