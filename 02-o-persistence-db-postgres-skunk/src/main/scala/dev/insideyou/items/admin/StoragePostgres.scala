package dev.insideyou
package shoppy
package items
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

  object SQL {
    import items.StoragePostgresImpl.SQL._
    import branding.StoragePostgresImpl.SQL._
    import categories.StoragePostgresImpl.SQL._

    val insertItem: Command[ItemId ~ CreateItem] =
      sql"""
        INSERT INTO items
        VALUES ($itemId, $itemName, $itemDesc, $money, $brandId, $categoryId)
       """.command.contramap {
        case id ~ i =>
          id ~ i.name ~ i.description ~ i.price ~ i.brandId ~ i.categoryId
      }

    val updateItem: Command[UpdateItem] =
      sql"""
        UPDATE items
        SET price = $money
        WHERE uuid = $itemId
       """.command.contramap(i => i.price ~ i.id)
  }
}
