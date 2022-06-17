package dev.insideyou
package shoppy
package branding

import cats.effect._
import skunk._
import skunk.codec.all._
import skunk.implicits._

object StoragePostgresImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Storage[F] =
    new Storage[F] {
      override def findAllBrands: F[List[Brand]] =
        postgres.use(_.execute(SQL.selectAll))
    }

  object SQL {
    val brandId: Codec[BrandId] =
      uuid.imap[BrandId](BrandId(_))(_.value)

    val brandName: Codec[BrandName] =
      varchar.imap[BrandName](BrandName(_))(_.value)

    val codec: Codec[Brand] =
      (brandId ~ brandName).imap {
        case i ~ n => Brand(i, n)
      }(b => b.uuid ~ b.name)

    val selectAll: Query[Void, Brand] =
      sql"""
        SELECT * FROM brands
       """.query(codec)
  }
}
