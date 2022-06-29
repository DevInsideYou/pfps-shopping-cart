package dev.insideyou
package shoppy
package items
package admin

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.implicits._

object PersistenceImpl {
  def make[F[_]: GenUUID: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Persistence[F] =
    new Persistence[F] {
      override def createItem(item: CreateItem): F[ItemId] =
        postgres.use { session =>
          session.prepare(SQL.insertItem).use { cmd =>
            ID.make[F, ItemId].flatMap { id =>
              cmd.execute(id ~ item).as(id)
            }
          }
        }

      override def updateItem(item: UpdateItem): F[Unit] =
        postgres.use { session =>
          session.prepare(SQL.updateItem).use { cmd =>
            cmd.execute(item).void
          }
        }
    }
}
