package dev.insideyou
package shoppy
package items
package admin

trait Boundary[F[_]] {
  def create(item: CreateItem): F[ItemId]
  def update(item: UpdateItem): F[Unit]
}

object BoundaryImpl {
  def make[F[_]](dependencies: Dependencies[F]): Boundary[F] =
    new Boundary[F] {
      override def create(item: CreateItem): F[ItemId] =
        dependencies.createItem(item)

      override def update(item: UpdateItem): F[Unit] =
        dependencies.updateItem(item)
    }

  trait Dependencies[F[_]] extends Persistence[F]

  def make[F[_]](persistence: Persistence[F]): Boundary[F] =
    make {
      new Dependencies[F] {
        override def createItem(item: CreateItem): F[ItemId] =
          persistence.createItem(item)

        override def updateItem(item: UpdateItem): F[Unit] =
          persistence.updateItem(item)
      }
    }

}
