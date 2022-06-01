package dev.insideyou
package shoppy
package categories

trait Boundary[F[_]] {
  def findAll: F[List[Category]]
}

object BoundaryImpl {
  def make[F[_]](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      def findAll: F[List[Category]] =
        gate.findAllCategories
    }
}
