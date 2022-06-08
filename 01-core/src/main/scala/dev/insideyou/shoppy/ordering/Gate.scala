package dev.insideyou
package shoppy
package ordering

trait Gate[F[_]] extends Storage[F]
object Gate {
  def make[F[_]](storage: Storage[F]): Gate[F] =
    new Gate[F] {
      override def getOrder(userId: UserId, orderId: OrderId): F[Option[Order]] =
        storage.getOrder(userId, orderId)

      override def findOrderBy(userId: UserId): F[List[Order]] =
        storage.findOrderBy(userId)
    }
}

trait Storage[F[_]] {
  def getOrder(userId: UserId, orderId: OrderId): F[Option[Order]]
  def findOrderBy(userId: UserId): F[List[Order]]
}
