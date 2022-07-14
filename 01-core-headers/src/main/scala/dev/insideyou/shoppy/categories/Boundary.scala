package dev.insideyou
package shoppy
package categories

trait Boundary[F[_]] {
  def findAll: F[List[Category]]
}
