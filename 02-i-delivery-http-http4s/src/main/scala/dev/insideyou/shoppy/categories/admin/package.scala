package dev.insideyou
package shoppy
package categories

import derevo.cats._
import derevo.circe.magnolia.decoder
import derevo.derive
import dev.insideyou.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.refined._
import io.estatico.newtype.macros.newtype

package object admin {
  @derive(decoder, queryParam, show)
  @newtype
  final case class CategoryParam(name: NonEmptyString) {
    def toDomain: CategoryName = CategoryName(name.toLowerCase.capitalize)
  }
}
