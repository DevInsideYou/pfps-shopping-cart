package dev.insideyou
package shoppy
package items
package admin

trait Boundary[F[_]] {
  def create(item: CreateItem): F[ItemId]
  def update(item: UpdateItem): F[Unit]
}

object BoundaryImpl {
  def make[F[_]](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      override def create(item: CreateItem): F[ItemId] =
        gate.createItem(item)

      override def update(item: UpdateItem): F[Unit] =
        gate.updateItem(item)
    }
}
