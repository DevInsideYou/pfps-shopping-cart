package dev.insideyou
package shoppy
package health

import scala.concurrent.duration._

import health_package_object._

trait Gate[F[_]] extends Redis[F] with Storage[F]

object Gate {
  def make[F[_]](storage: Storage[F], redis: Redis[F]): Gate[F] =
    new Gate[F] {
      override def redisStatus(timeout: FiniteDuration): F[RedisStatus] =
        redis.redisStatus(timeout)

      override def postgresStatus(timeout: FiniteDuration): F[PostgresStatus] =
        storage.postgresStatus(timeout)
    }
}

trait Redis[F[_]] {
  def redisStatus(timeout: FiniteDuration): F[RedisStatus]
}

trait Storage[F[_]] {
  def postgresStatus(timeout: FiniteDuration): F[PostgresStatus]
}
