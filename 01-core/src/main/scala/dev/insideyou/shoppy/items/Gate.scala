package dev.insideyou
package shoppy
package items

trait Gate[F[_]] extends Storage[F]

object Gate {
  def make[F[_]](storage: Storage[F]): Gate[F] =
    new Gate[F] {
      override def findAllItems: F[List[Item]] =
        storage.findAllItems

      override def findItemsBy(brand: branding.BrandName): F[List[Item]] =
        storage.findItemsBy(brand)
    }
}

// TODO rename to Persistence
trait Storage[F[_]] {
  def findAllItems: F[List[Item]]
  def findItemsBy(brand: branding.BrandName): F[List[Item]]
}
