package dev.insideyou
package shoppy
package categories
package admin

trait Persistence[F[_]] {
  def createCategory(name: CategoryName): F[CategoryId]
}
