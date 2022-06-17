package dev.insideyou
package shoppy
package health

import scala.concurrent.duration._

import cats.effect._
import cats.effect.implicits._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands

import health_package_object._

object RedisImpl {
  def make[F[_]: Temporal](
      redis: RedisCommands[F, String, String]
  ): Redis[F] =
    new Redis[F] {
      override def redisStatus(timeout: FiniteDuration): F[RedisStatus] =
        redis.ping
          .map(_.nonEmpty)
          .timeout(1.second)
          .map(Status.Bool.reverseGet)
          .orElse(Status.Unreachable.pure[F].widen)
          .map(RedisStatus.apply)
    }
}
