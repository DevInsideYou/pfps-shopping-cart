package dev.insideyou
package shoppy
package health

import scala.concurrent.duration.FiniteDuration

import cats.effect._
import cats.effect.implicits._
import cats.syntax.all._
import skunk._
import skunk.codec.all._
import skunk.implicits._

import health_package_object._

object StoragePostgresImpl {
  def make[F[_]: Temporal](
      postgres: Resource[F, Session[F]]
  ): Storage[F] =
    new Storage[F] {
      override def postgresStatus(timeout: FiniteDuration): F[PostgresStatus] =
        postgres
          .use(_.execute(SQL.query))
          .map(_.nonEmpty)
          .timeout(timeout)
          .map(Status.Bool.reverseGet)
          .orElse(Status.Unreachable.pure[F].widen)
          .map(PostgresStatus.apply)
    }

  object SQL {
    val query: Query[Void, Int] =
      sql"SELECT pid FROM pg_stat_activity".query(int4)
  }
}
