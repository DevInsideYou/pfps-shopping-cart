package dev.insideyou
package shoppy
package categories

trait Boundary[F[_]] {
  def findAll: F[List[Category]]
}

object BoundaryImpl {
  def make[F[_]](dependencies: Dependencies[F]): Boundary[F] =
    new Boundary[F] {
      def findAll: F[List[Category]] =
        dependencies.findAllCategories
    }

  trait Dependencies[F[_]] extends Persistence[F]

  def make[F[_]](persistence: Persistence[F]): Boundary[F] =
    make {
      new Dependencies[F] {
        override def findAllCategories: F[List[Category]] =
          persistence.findAllCategories
      }
    }
}
