package dev.insideyou
package shoppy
package health

import scala.concurrent.duration.FiniteDuration

import cats.effect._
import cats.effect.implicits._
import cats.syntax.all._
import skunk._

import health_package_object._

object PersistenceImpl {
  def make[F[_]: Temporal](
      postgres: Resource[F, Session[F]]
  ): Persistence[F] =
    new Persistence[F] {
      override def postgresStatus(timeout: FiniteDuration): F[PostgresStatus] =
        postgres
          .use(_.execute(SQL.query))
          .map(_.nonEmpty)
          .timeout(timeout)
          .map(Status.Bool.reverseGet)
          .orElse(Status.Unreachable.pure[F].widen)
          .map(PostgresStatus.apply)
    }
}
