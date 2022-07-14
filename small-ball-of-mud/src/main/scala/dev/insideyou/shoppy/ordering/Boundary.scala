package dev.insideyou
package shoppy
package ordering

trait Boundary[F[_]] {
  def get(userId: UserId, orderId: OrderId): F[Option[Order]]
  def findBy(userId: UserId): F[List[Order]]
}
