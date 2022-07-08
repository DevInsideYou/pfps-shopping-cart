package dev.insideyou
package shoppy
package branding

trait Persistence[F[_]] {
  def findAllBrands: F[List[Brand]]
}
