package dev.insideyou
package shoppy
package branding
package admin

import cats.effect._
import cats.syntax.all._
import skunk._

object PersistenceImpl {
  def make[F[_]: GenUUID: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Persistence[F] =
    new Persistence[F] {
      override def createBrand(name: BrandName): F[BrandId] =
        postgres.use { session =>
          session.prepare(SQL.insertBrand).use { cmd =>
            ID.make[F, BrandId].flatMap { id =>
              cmd.execute(Brand(id, name)).as(id)
            }
          }
        }
    }
}
