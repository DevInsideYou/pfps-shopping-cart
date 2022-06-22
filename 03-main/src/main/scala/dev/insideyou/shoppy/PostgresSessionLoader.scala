package dev.insideyou
package shoppy

import cats.effect._
import cats.syntax.all._
import ciris._
import ciris.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import org.typelevel.log4cats
import skunk.SessionPool

object PostgresSessionLoader {
  def load[F[_]: Async: log4cats.Logger: std.Console]: F[SessionPool[F]] = {
    implicit val logger: Logger[F] = LoggerImpl.make(implicitly, "PostgresSessionLoader")
    implicit val checkConnection   = CheckPostgresConnection.make[F]

    loadConfig.map(PostgresSession.make[F])
  }

  def loadConfig[F[_]: Async]: F[PostgreSQLConfig] =
    env("SC_POSTGRES_PASSWORD")
      .as[NonEmptyString]
      .secret
      .map { password =>
        PostgreSQLConfig(
          host = "localhost",
          port = 5432,
          user = "postgres",
          password = password.value,
          database = "store",
          max = 10
        )
      }
      .load[F]
}
