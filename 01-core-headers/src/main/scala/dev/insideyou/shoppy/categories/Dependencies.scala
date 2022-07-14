package dev.insideyou
package shoppy
package categories

trait Persistence[F[_]] {
  def findAllCategories: F[List[Category]]
}
