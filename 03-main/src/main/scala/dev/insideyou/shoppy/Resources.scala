package dev.insideyou
package shoppy

import cats.effect._
import dev.profunktor.redis4cats.RedisCommands
import skunk._

final case class Resources[F[_]](
    redis: RedisCommands[F, String, String],
    postgres: Resource[F, Session[F]],
    httpClientResources: HttpClientResources[F]
)
