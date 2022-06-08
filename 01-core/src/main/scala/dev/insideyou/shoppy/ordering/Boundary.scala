package dev.insideyou
package shoppy
package ordering

trait Boundary[F[_]] {
  def get(userId: UserId, orderId: OrderId): F[Option[Order]]
  def findBy(userId: UserId): F[List[Order]]
}

object BoundaryImpl {
  def make[F[_]](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      override def get(userId: UserId, orderId: OrderId): F[Option[Order]] =
        gate.getOrder(userId, orderId)

      override def findBy(userId: UserId): F[List[Order]] =
        gate.findOrderBy(userId)
    }
}
