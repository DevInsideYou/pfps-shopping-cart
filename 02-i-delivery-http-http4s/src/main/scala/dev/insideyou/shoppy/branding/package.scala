package dev.insideyou
package shoppy
package branding

import derevo.cats._
import derevo.circe.magnolia.decoder
import derevo.derive
import dev.insideyou.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.refined._
import io.estatico.newtype.macros.newtype

object package_object_2 {
  @derive(decoder, queryParam, show)
  @newtype
  final case class BrandParam(name: NonEmptyString) {
    def toDomain: BrandName = BrandName(name.toLowerCase.capitalize)
  }
}
