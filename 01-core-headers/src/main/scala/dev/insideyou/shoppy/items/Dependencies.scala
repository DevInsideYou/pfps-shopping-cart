package dev.insideyou
package shoppy
package items

trait Persistence[F[_]] {
  def findAllItems: F[List[Item]]
  def findItemsBy(brand: branding.BrandName): F[List[Item]]
}
