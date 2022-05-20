package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats.effect._
import ciris._
import ciris.refined._
import eu.timepit.refined.types.string.NonEmptyString

object HasConfigImpl {
  def make[F[_]: Async]: HasConfig[F, Config] =
    new HasConfig[F, Config] {
      override def config: F[Config] =
        env("SC_ACCESS_TOKEN_SECRET_KEY") // TODO we're already loading this, so let's load the entire config at once
          .as[JwtAccessTokenKeyConfig]
          .secret
          .map(a => Config(a.value))
          .load[F]
    }

  private implicit lazy val a: ConfigDecoder[String, JwtAccessTokenKeyConfig] =
    ConfigDecoder[String, NonEmptyString].map(JwtAccessTokenKeyConfig.apply)
}
