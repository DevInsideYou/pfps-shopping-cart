package dev.insideyou
package shoppy
package categories

import cats.effect._
import skunk._

object PersistenceImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Persistence[F] =
    new Persistence[F] {
      override def findAllCategories: F[List[Category]] =
        postgres.use(_.execute(SQL.selectAll))
    }
}
