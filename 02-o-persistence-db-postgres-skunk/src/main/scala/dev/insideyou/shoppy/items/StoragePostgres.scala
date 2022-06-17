package dev.insideyou
package shoppy
package items

import cats.effect._
import skunk._
import skunk.codec.all._
import skunk.implicits._
import squants.market._

object StoragePostgresImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  )(implicit C: fs2.Compiler[F, F]): Storage[F] =
    new Storage[F] {
      override def findAllItems: F[List[Item]] =
        postgres.use(_.execute(SQL.selectAll))

      override def findItemsBy(brand: branding.BrandName): F[List[Item]] =
        postgres.use { session =>
          session.prepare(SQL.selectByBrand).use { ps =>
            ps.stream(brand, 1024).compile.toList
          }
        }
    }

  object SQL {
    import branding.StoragePostgresImpl.SQL._
    import categories.StoragePostgresImpl.SQL._

    val itemId: Codec[ItemId] =
      uuid.imap[ItemId](ItemId(_))(_.value)

    val itemName: Codec[ItemName] =
      varchar.imap[ItemName](ItemName(_))(_.value)

    val itemDesc: Codec[ItemDescription] =
      varchar.imap[ItemDescription](ItemDescription(_))(_.value)

    val money: Codec[Money] = numeric.imap[Money](USD(_))(_.amount)

    val decoder: Decoder[Item] =
      (itemId ~ itemName ~ itemDesc ~ money ~ brandId ~ brandName ~ categoryId ~ categoryName).map {
        case i ~ n ~ d ~ p ~ bi ~ bn ~ ci ~ cn =>
          Item(i, n, d, p, branding.Brand(bi, bn), categories.Category(ci, cn))
      }

    val selectAll: Query[Void, Item] =
      sql"""
        SELECT i.uuid, i.name, i.description, i.price, b.uuid, b.name, c.uuid, c.name
        FROM items AS i
        INNER JOIN brands AS b ON i.brand_id = b.uuid
        INNER JOIN categories AS c ON i.category_id = c.uuid
       """.query(decoder)

    val selectByBrand: Query[branding.BrandName, Item] =
      sql"""
        SELECT i.uuid, i.name, i.description, i.price, b.uuid, b.name, c.uuid, c.name
        FROM items AS i
        INNER JOIN brands AS b ON i.brand_id = b.uuid
        INNER JOIN categories AS c ON i.category_id = c.uuid
        WHERE b.name LIKE $brandName
       """.query(decoder)
  }
}
