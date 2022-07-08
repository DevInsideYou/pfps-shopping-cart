package dev.insideyou
package shoppy
package items
package admin

trait Persistence[F[_]] {
  def createItem(item: CreateItem): F[ItemId]
  def updateItem(item: UpdateItem): F[Unit]
}
