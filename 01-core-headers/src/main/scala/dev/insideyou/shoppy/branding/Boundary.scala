package dev.insideyou
package shoppy
package branding

trait Boundary[F[_]] {
  def findAll: F[List[Brand]]
}
