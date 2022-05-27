package dev.insideyou
package shoppy
package branding

trait Boundary[F[_]] {
  def findAll: F[List[Brand]]
}

object BoundaryImpl {
  def make[F[_]](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      def findAll: F[List[Brand]] =
        gate.findAllBrands
    }
}
