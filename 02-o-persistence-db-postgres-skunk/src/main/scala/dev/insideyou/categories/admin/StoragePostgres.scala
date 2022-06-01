package dev.insideyou
package shoppy
package categories
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

      override def createCategory(name: CategoryName): F[CategoryId] =
        postgres.use { session =>
          session.prepare(insertCategory).use { cmd =>
            ID.make[F, CategoryId].flatMap { id =>
              cmd.execute(Category(id, name)).as(id)
            }
          }
        }
    }

  private object SQL {
    import categories.StoragePostgresImpl.SQL._

    val insertCategory: Command[Category] =
      sql"""
        INSERT INTO categories
        VALUES ($codec)
        """.command
  }
}
