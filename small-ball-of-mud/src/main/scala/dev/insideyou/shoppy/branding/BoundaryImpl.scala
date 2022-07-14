package dev.insideyou
package shoppy
package branding

object BoundaryImpl {
  def make[F[_]](dependencies: Dependencies[F]): Boundary[F] =
    new Boundary[F] {
      def findAll: F[List[Brand]] =
        dependencies.findAllBrands
    }

  trait Dependencies[F[_]] extends Persistence[F]

  def make[F[_]](persistence: Persistence[F]): Boundary[F] =
    make {
      new Dependencies[F] {
        override def findAllBrands: F[List[Brand]] =
          persistence.findAllBrands
      }
    }
}
