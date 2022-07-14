package dev.insideyou
package shoppy
package checkout

trait Boundary[F[_]] {
  def process(userId: UserId, card: Card): F[ordering.OrderId]
}
