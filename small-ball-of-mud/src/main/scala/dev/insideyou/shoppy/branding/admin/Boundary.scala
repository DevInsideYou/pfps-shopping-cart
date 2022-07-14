package dev.insideyou
package shoppy
package branding
package admin

trait Boundary[F[_]] {
  def create(name: BrandName): F[BrandId]
}
