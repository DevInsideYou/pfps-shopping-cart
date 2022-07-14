package dev.insideyou
package shoppy

import cats._
import cats.effect._
import dev.profunktor.redis4cats.effect._
import dev.profunktor.redis4cats.{ Redis => Redis4Cats, RedisCommands }
import eu.timepit.refined.auto._

object RedisSession {
  def make[F[_]: MonadThrow: MkRedis: CheckRedisConnection](
      c: Redis.Config
  ): Resource[F, RedisCommands[F, String, String]] =
    Redis4Cats[F]
      .utf8(c.uri.value)
      .evalTap(CheckRedisConnection[F].checkRedisConnection)
}
