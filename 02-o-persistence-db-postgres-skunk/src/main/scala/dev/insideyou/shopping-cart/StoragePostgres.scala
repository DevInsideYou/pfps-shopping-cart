package dev.insideyou
package shoppy
package shopping_cart

import cats.effect._
import skunk._
import skunk.implicits._

object StoragePostgresImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Storage[F] =
    new Storage[F] {
      override def findItemsById(itemId: items.ItemId): F[Option[items.Item]] =
        postgres.use { session =>
          session.prepare(SQL.selectById).use { ps =>
            ps.option(itemId)
          }
        }
    }

  object SQL {
    import items.StoragePostgresImpl.SQL._

    val selectById: Query[items.ItemId, items.Item] =
      sql"""
        SELECT i.uuid, i.name, i.description, i.price, b.uuid, b.name, c.uuid, c.name
        FROM items AS i
        INNER JOIN brands AS b ON i.brand_id = b.uuid
        INNER JOIN categories AS c ON i.category_id = c.uuid
        WHERE i.uuid = $itemId
       """.query(decoder)
  }
}
