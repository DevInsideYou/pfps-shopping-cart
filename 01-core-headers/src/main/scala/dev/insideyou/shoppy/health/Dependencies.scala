package dev.insideyou
package shoppy
package health

import scala.concurrent.duration._

import health_package_object._

trait Redis[F[_]] {
  def redisStatus(timeout: FiniteDuration): F[RedisStatus]
}

trait Persistence[F[_]] {
  def postgresStatus(timeout: FiniteDuration): F[PostgresStatus]
}
