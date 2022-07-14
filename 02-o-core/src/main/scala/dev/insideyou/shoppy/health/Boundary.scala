package dev.insideyou
package shoppy
package health

import scala.concurrent.duration._
import cats._
import cats.syntax.all._
import health_package_object._

object BoundaryImpl {
  def make[F[_]: NonEmptyParallel](dependencies: Dependencies[F]): Boundary[F] =
    new Boundary[F] {
      override lazy val status: F[AppStatus] =
        (
          dependencies.redisStatus(timeout),
          dependencies.postgresStatus(timeout)
        ).parMapN(AppStatus)

      private lazy val timeout =
        1.second
    }

  trait Dependencies[F[_]] extends Redis[F] with Persistence[F]

  def make[F[_]: NonEmptyParallel](persistence: Persistence[F], redis: Redis[F]): Boundary[F] =
    make {
      new Dependencies[F] {
        override def redisStatus(timeout: FiniteDuration): F[RedisStatus] =
          redis.redisStatus(timeout)

        override def postgresStatus(timeout: FiniteDuration): F[PostgresStatus] =
          persistence.postgresStatus(timeout)
      }
    }

}
