package dev.insideyou
package shoppy
package categories
package admin

import cats.effect._
import cats.syntax.all._
import skunk._

object PersistenceImpl {
  def make[F[_]: GenUUID: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Persistence[F] =
    new Persistence[F] {
      override def createCategory(name: CategoryName): F[CategoryId] =
        postgres.use { session =>
          session.prepare(SQL.insertCategory).use { cmd =>
            ID.make[F, CategoryId].flatMap { id =>
              cmd.execute(Category(id, name)).as(id)
            }
          }
        }
    }
}
