package dev.insideyou
package shoppy
package categories

trait Gate[F[_]] extends Storage[F]

object Gate {
  def make[F[_]](storage: Storage[F]): Gate[F] =
    new Gate[F] {
      override def findAllCategories: F[List[Category]] =
        storage.findAllCategories
    }
}

// TODO rename to Persistence
trait Storage[F[_]] {
  def findAllCategories: F[List[Category]]
}
