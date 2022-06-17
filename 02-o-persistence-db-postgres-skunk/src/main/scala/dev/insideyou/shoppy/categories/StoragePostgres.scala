package dev.insideyou
package shoppy
package categories

import cats.effect._
import skunk._
import skunk.codec.all._
import skunk.implicits._

object StoragePostgresImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Storage[F] =
    new Storage[F] {
      override def findAllCategories: F[List[Category]] =
        postgres.use(_.execute(SQL.selectAll))
    }

  object SQL {
    val categoryId: Codec[CategoryId] =
      uuid.imap[CategoryId](CategoryId(_))(_.value)

    val categoryName: Codec[CategoryName] =
      varchar.imap[CategoryName](CategoryName(_))(_.value)

    val codec: Codec[Category] =
      (categoryId ~ categoryName).imap {
        case i ~ n => Category(i, n)
      }(b => b.uuid ~ b.name)

    val selectAll: Query[Void, Category] =
      sql"""
        SELECT * FROM categories
       """.query(codec)
  }
}
