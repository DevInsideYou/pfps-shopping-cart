package dev.insideyou
package shoppy
package branding

trait Gate[F[_]] extends Persistence[F]

object Gate {
  def make[F[_]](persistence: Persistence[F]): Gate[F] =
    new Gate[F] {
      override def findAllBrands: F[List[Brand]] =
        persistence.findAllBrands
    }
}

trait Persistence[F[_]] {
  def findAllBrands: F[List[Brand]]
}
