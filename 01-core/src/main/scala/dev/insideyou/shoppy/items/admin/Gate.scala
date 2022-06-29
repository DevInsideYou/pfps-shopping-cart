package dev.insideyou
package shoppy
package items
package admin

trait Gate[F[_]] extends Persistence[F]
object Gate {
  def make[F[_]](persistence: Persistence[F]): Gate[F] =
    new Gate[F] {
      override def createItem(item: CreateItem): F[ItemId] =
        persistence.createItem(item)

      override def updateItem(item: UpdateItem): F[Unit] =
        persistence.updateItem(item)
    }
}

trait Persistence[F[_]] {
  def createItem(item: CreateItem): F[ItemId]
  def updateItem(item: UpdateItem): F[Unit]
}
