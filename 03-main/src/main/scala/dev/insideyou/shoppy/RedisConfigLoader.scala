package dev.insideyou
package shoppy

import cats._
import eu.timepit.refined.auto._

object RedisConfigLoader {
  def load[F[_]: Applicative](appEnvironment: AppEnvironment): F[Redis.Config] =
    Applicative[F].pure {
      appEnvironment match {
        case AppEnvironment.Prod =>
          Redis.Config(
            Redis.Config.URI("redis://localhost")
          )
        case AppEnvironment.Test =>
          Redis.Config(
            Redis.Config.URI("redis://10.123.154.176")
          )
      }
    }
}
