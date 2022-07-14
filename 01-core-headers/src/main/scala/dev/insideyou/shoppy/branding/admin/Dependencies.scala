package dev.insideyou
package shoppy
package branding
package admin

trait Persistence[F[_]] {
  def createBrand(name: BrandName): F[BrandId]
}
