package dev.insideyou
package shoppy
package items

trait Boundary[F[_]] {
  def findAll: F[List[Item]]
  def findBy(brand: branding.BrandName): F[List[Item]]
}

object BoundaryImpl {
  def make[F[_]](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      override lazy val findAll: F[List[Item]] =
        gate.findAllItems

      override def findBy(brand: branding.BrandName): F[List[Item]] =
        gate.findItemsBy(brand)
    }
}
