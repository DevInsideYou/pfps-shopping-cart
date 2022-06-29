package dev.insideyou
package shoppy
package items

trait Gate[F[_]] extends Persistence[F]

object Gate {
  def make[F[_]](persistence: Persistence[F]): Gate[F] =
    new Gate[F] {
      override def findAllItems: F[List[Item]] =
        persistence.findAllItems

      override def findItemsBy(brand: branding.BrandName): F[List[Item]] =
        persistence.findItemsBy(brand)
    }
}

trait Persistence[F[_]] {
  def findAllItems: F[List[Item]]
  def findItemsBy(brand: branding.BrandName): F[List[Item]]
}
