package dev.insideyou
package shoppy
package ordering

trait Gate[F[_]] extends Persistence[F]
object Gate {
  def make[F[_]](persistence: Persistence[F]): Gate[F] =
    new Gate[F] {
      override def getOrder(userId: UserId, orderId: OrderId): F[Option[Order]] =
        persistence.getOrder(userId, orderId)

      override def findOrderBy(userId: UserId): F[List[Order]] =
        persistence.findOrderBy(userId)
    }
}

trait Persistence[F[_]] {
  def getOrder(userId: UserId, orderId: OrderId): F[Option[Order]]
  def findOrderBy(userId: UserId): F[List[Order]]
}
