package dev.insideyou
package shoppy
package branding
package admin

trait Gate[F[_]] extends Persistence[F]

object Gate {
  def make[F[_]](persistence: Persistence[F]): Gate[F] =
    new Gate[F] {
      override def createBrand(name: BrandName): F[BrandId] =
        persistence.createBrand(name)
    }
}

trait Persistence[F[_]] {
  def createBrand(name: BrandName): F[BrandId]
}
