package dev.insideyou
package shoppy
package categories
package admin

trait Gate[F[_]] extends Storage[F]

object Gate {
  def make[F[_]](storage: Storage[F]): Gate[F] =
    new Gate[F] {
      override def createCategory(name: CategoryName): F[CategoryId] =
        storage.createCategory(name)
    }
}

// TODO rename to Persistence
trait Storage[F[_]] {
  def createCategory(name: CategoryName): F[CategoryId]
}
