package dev.insideyou
package shoppy

import cats.effect.Async
import ciris._

object AppEnvironmentConfigLoader {
  def load[F[_]: Async]: F[AppEnvironment] =
    env("SC_APP_ENV").as[AppEnvironment].load[F]
}
