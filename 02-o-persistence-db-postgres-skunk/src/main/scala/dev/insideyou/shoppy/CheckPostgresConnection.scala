package dev.insideyou
package shoppy

import cats.effect._
import cats.syntax.all._
import eu.timepit.refined.auto._
import skunk._
import skunk.codec.text._
import skunk.implicits._

trait CheckPostgresConnection[F[_]] {
  def checkPostgresConnection(postgres: Resource[F, Session[F]]): F[Unit]
}

object CheckPostgresConnection {
  def make[F[_]: MonadCancelThrow: Logger]: CheckPostgresConnection[F] =
    new CheckPostgresConnection[F] {
      override def checkPostgresConnection(
          postgres: Resource[F, Session[F]]
      ): F[Unit] =
        postgres.use { session =>
          session.unique(sql"select version();".query(text)).flatMap { v =>
            Logger[F].info(s"Connected to Postgres $v")
          }
        }
    }

  def apply[F[_]: CheckPostgresConnection]: CheckPostgresConnection[F] = implicitly
}
