package dev.insideyou
package shoppy
package categories
package admin

trait Gate[F[_]] extends Persistence[F]

object Gate {
  def make[F[_]](persistence: Persistence[F]): Gate[F] =
    new Gate[F] {
      override def createCategory(name: CategoryName): F[CategoryId] =
        persistence.createCategory(name)
    }
}

trait Persistence[F[_]] {
  def createCategory(name: CategoryName): F[CategoryId]
}
