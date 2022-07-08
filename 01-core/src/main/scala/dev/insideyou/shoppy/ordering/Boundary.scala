package dev.insideyou
package shoppy
package ordering

trait Boundary[F[_]] {
  def get(userId: UserId, orderId: OrderId): F[Option[Order]]
  def findBy(userId: UserId): F[List[Order]]
}

object BoundaryImpl {
  def make[F[_]](dependencies: Dependencies[F]): Boundary[F] =
    new Boundary[F] {
      override def get(userId: UserId, orderId: OrderId): F[Option[Order]] =
        dependencies.getOrder(userId, orderId)

      override def findBy(userId: UserId): F[List[Order]] =
        dependencies.findOrderBy(userId)
    }

  trait Dependencies[F[_]] extends Persistence[F]

  def make[F[_]](persistence: Persistence[F]): Boundary[F] =
    make {
      new Dependencies[F] {
        override def getOrder(userId: UserId, orderId: OrderId): F[Option[Order]] =
          persistence.getOrder(userId, orderId)

        override def findOrderBy(userId: UserId): F[List[Order]] =
          persistence.findOrderBy(userId)
      }
    }

}
