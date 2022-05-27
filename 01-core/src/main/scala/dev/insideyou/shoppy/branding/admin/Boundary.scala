package dev.insideyou
package shoppy
package branding
package admin

trait Boundary[F[_]] {
  def create(name: BrandName): F[BrandId]
}

object BoundaryImpl {
  def make[F[_]](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      def create(name: BrandName): F[BrandId] =
        gate.createBrand(name)
    }
}
