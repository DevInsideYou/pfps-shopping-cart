package dev.insideyou
package shoppy
package branding
package admin

trait Gate[F[_]] extends Storage[F]

object Gate {
  def make[F[_]](storage: Storage[F]): Gate[F] =
    new Gate[F] {
      override def createBrand(name: BrandName): F[BrandId] =
        storage.createBrand(name)
    }
}

// TODO rename to Persistence
trait Storage[F[_]] {
  def createBrand(name: BrandName): F[BrandId]
}
