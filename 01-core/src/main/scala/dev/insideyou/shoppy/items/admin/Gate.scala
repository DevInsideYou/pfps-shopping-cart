package dev.insideyou
package shoppy
package items
package admin

trait Gate[F[_]] extends Storage[F]
object Gate {
  def make[F[_]](storage: Storage[F]): Gate[F] =
    new Gate[F] {
      override def createItem(item: CreateItem): F[ItemId] =
        storage.createItem(item)

      override def updateItem(item: UpdateItem): F[Unit] =
        storage.updateItem(item)
    }
}

// TODO rename to Postgress
trait Storage[F[_]] {
  def createItem(item: CreateItem): F[ItemId]
  def updateItem(item: UpdateItem): F[Unit]
}
