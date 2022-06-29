package dev.insideyou
package shoppy
package categories

trait Gate[F[_]] extends Persistence[F]

object Gate {
  def make[F[_]](persistence: Persistence[F]): Gate[F] =
    new Gate[F] {
      override def findAllCategories: F[List[Category]] =
        persistence.findAllCategories
    }
}

trait Persistence[F[_]] {
  def findAllCategories: F[List[Category]]
}
