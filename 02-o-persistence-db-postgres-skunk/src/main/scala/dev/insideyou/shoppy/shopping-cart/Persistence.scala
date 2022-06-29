package dev.insideyou
package shoppy
package shopping_cart

import cats.effect._
import skunk._

object PersistenceImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Persistence[F] =
    new Persistence[F] {
      override def findItemsById(itemId: items.ItemId): F[Option[items.Item]] =
        postgres.use { session =>
          session.prepare(SQL.selectById).use { ps =>
            ps.option(itemId)
          }
        }
    }
}
