package dev.insideyou
package shoppy

import java.util.UUID

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype

package object categories {
  @derive(eqv, show, uuid)
  @newtype
  final case class CategoryId(value: UUID)

  @derive(eqv, show)
  @newtype
  final case class CategoryName(value: String) {
    def toCategory(categoryId: CategoryId): Category =
      Category(categoryId, this)
  }
}
