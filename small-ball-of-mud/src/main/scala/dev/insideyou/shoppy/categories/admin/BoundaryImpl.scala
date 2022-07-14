package dev.insideyou
package shoppy
package categories
package admin

object BoundaryImpl {
  def make[F[_]](dependencies: Dependencies[F]): Boundary[F] =
    new Boundary[F] {
      def create(name: CategoryName): F[CategoryId] =
        dependencies.createCategory(name)
    }

  trait Dependencies[F[_]] extends Persistence[F]

  def make[F[_]](persistence: Persistence[F]): Boundary[F] =
    make {
      new Dependencies[F] {
        override def createCategory(name: CategoryName): F[CategoryId] =
          persistence.createCategory(name)
      }
    }
}
