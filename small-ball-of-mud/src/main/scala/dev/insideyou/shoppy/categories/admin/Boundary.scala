package dev.insideyou
package shoppy
package categories
package admin

trait Boundary[F[_]] {
  def create(name: CategoryName): F[CategoryId]
}
