package dev.insideyou
package shoppy
package branding

import cats.effect._
import skunk._

object PersistenceImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Persistence[F] =
    new Persistence[F] {
      override def findAllBrands: F[List[Brand]] =
        postgres.use(_.execute(SQL.selectAll))
    }
}
