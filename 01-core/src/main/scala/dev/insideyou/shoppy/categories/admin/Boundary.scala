package dev.insideyou
package shoppy
package categories
package admin

trait Boundary[F[_]] {
  def create(name: CategoryName): F[CategoryId]
}

object BoundaryImpl {
  def make[F[_]](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      def create(name: CategoryName): F[CategoryId] =
        gate.createCategory(name)
    }
}
