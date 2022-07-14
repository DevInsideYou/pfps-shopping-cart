package dev.insideyou
package shoppy
package ordering

trait Persistence[F[_]] {
  def getOrder(userId: UserId, orderId: OrderId): F[Option[Order]]
  def findOrderBy(userId: UserId): F[List[Order]]
}
