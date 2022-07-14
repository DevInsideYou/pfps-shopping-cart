package dev.insideyou
package shoppy
package items
package admin

trait Boundary[F[_]] {
  def create(item: CreateItem): F[ItemId]
  def update(item: UpdateItem): F[Unit]
}
