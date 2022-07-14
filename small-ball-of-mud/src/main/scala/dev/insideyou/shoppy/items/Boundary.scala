package dev.insideyou
package shoppy
package items

trait Boundary[F[_]] {
  def findAll: F[List[Item]]
  def findBy(brand: branding.BrandName): F[List[Item]]
}
