package dev.insideyou
package shoppy
package ordering

import cats.effect._
import skunk._
import skunk.implicits._

object PersistenceImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  )(implicit C: fs2.Compiler[F, F]): Persistence[F] =
    new Persistence[F] {
      override def getOrder(userId: UserId, orderId: OrderId): F[Option[Order]] =
        postgres.use { session =>
          session.prepare(SQL.selectByUserIdAndOrderId).use { q =>
            q.option(userId ~ orderId)
          }
        }

      override def findOrderBy(userId: UserId): F[List[Order]] =
        postgres.use { session =>
          session.prepare(SQL.selectByUserId).use { q =>
            q.stream(userId, 1024).compile.toList
          }
        }
    }
}
