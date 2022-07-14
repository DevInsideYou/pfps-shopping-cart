package dev.insideyou
package shoppy
package branding
package admin

object BoundaryImpl {
  def make[F[_]](dependencies: Dependencies[F]): Boundary[F] =
    new Boundary[F] {
      def create(name: BrandName): F[BrandId] =
        dependencies.createBrand(name)
    }

  trait Dependencies[F[_]] extends Persistence[F]

  def make[F[_]](persistence: Persistence[F]): Boundary[F] =
    make {
      new Dependencies[F] {
        override def createBrand(name: BrandName): F[BrandId] =
          persistence.createBrand(name)
      }
    }
}
