package dev.insideyou
package shoppy
package branding
package admin

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.implicits._

object StoragePostgresImpl {
  def make[F[_]: GenUUID: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Storage[F] =
    new Storage[F] {
      import SQL._

      override def createBrand(name: BrandName): F[BrandId] =
        postgres.use { session =>
          session.prepare(insertBrand).use { cmd =>
            ID.make[F, BrandId].flatMap { id =>
              cmd.execute(Brand(id, name)).as(id)
            }
          }
        }
    }

  private object SQL {
    import branding.StoragePostgresImpl.SQL._

    val insertBrand: Command[Brand] =
      sql"""
        INSERT INTO brands
        VALUES ($codec)
        """.command
  }
}
