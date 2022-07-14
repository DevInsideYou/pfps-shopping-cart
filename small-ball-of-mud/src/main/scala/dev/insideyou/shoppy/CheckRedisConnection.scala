package dev.insideyou
package shoppy

import cats._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands

trait CheckRedisConnection[F[_]] {
  def checkRedisConnection(redis: RedisCommands[F, String, String]): F[Unit]
}

object CheckRedisConnection {
  def make[F[_]: Monad: Logger]: CheckRedisConnection[F] =
    new CheckRedisConnection[F] {
      override def checkRedisConnection(redis: RedisCommands[F, String, String]): F[Unit] =
        redis.info.flatMap {
          _.get("redis_version").traverse_ { v =>
            Logger[F].info(s"Connected to Redis $v")
          }
        }
    }

  def apply[F[_]: CheckRedisConnection]: CheckRedisConnection[F] = implicitly
}
