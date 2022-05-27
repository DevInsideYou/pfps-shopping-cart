package dev.insideyou
package shoppy
package branding

trait Gate[F[_]] extends Storage[F]

object Gate {
  def make[F[_]](storage: Storage[F]): Gate[F] =
    new Gate[F] {
      override def findAllBrands: F[List[Brand]] =
        storage.findAllBrands
    }
}

// TODO rename to Persistence
trait Storage[F[_]] {
  def findAllBrands: F[List[Brand]]
}
