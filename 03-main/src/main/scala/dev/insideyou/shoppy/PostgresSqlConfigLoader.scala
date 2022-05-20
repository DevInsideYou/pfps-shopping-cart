package dev.insideyou
package shoppy

import cats.effect.Async
import ciris._
import ciris.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString

object PostgresSqlConfigLoader {
  def load[F[_]: Async]: F[PostgreSQLConfig] =
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
