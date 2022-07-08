package dev.insideyou
package shoppy
package items

trait Boundary[F[_]] {
  def findAll: F[List[Item]]
  def findBy(brand: branding.BrandName): F[List[Item]]
}

object BoundaryImpl {
  def make[F[_]](dependencies: Dependencies[F]): Boundary[F] =
    new Boundary[F] {
      override lazy val findAll: F[List[Item]] =
        dependencies.findAllItems

      override def findBy(brand: branding.BrandName): F[List[Item]] =
        dependencies.findItemsBy(brand)
    }

  trait Dependencies[F[_]] extends Persistence[F]

  def make[F[_]](persistence: Persistence[F]): Boundary[F] =
    make {
      new Dependencies[F] {
        override def findAllItems: F[List[Item]] =
          persistence.findAllItems

        override def findItemsBy(brand: branding.BrandName): F[List[Item]] =
          persistence.findItemsBy(brand)
      }
    }

}
