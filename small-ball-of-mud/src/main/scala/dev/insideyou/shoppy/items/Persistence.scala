package dev.insideyou
package shoppy
package items

import cats.effect._
import skunk._

object PersistenceImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  )(implicit C: fs2.Compiler[F, F]): Persistence[F] =
    new Persistence[F] {
      override def findAllItems: F[List[Item]] =
        postgres.use(_.execute(SQL.selectAll))

      override def findItemsBy(brand: branding.BrandName): F[List[Item]] =
        postgres.use { session =>
          session.prepare(SQL.selectByBrand).use { ps =>
            ps.stream(brand, 1024).compile.toList
          }
        }
    }
}
