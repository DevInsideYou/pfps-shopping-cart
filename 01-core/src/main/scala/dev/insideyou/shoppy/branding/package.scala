package dev.insideyou
package shoppy

import java.util.UUID

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype

package object branding {
  @derive(eqv, show, uuid)
  @newtype
  final case class BrandId(value: UUID)

  @derive(eqv, show)
  @newtype
  final case class BrandName(value: String) {
    def toBrand(brandId: BrandId): Brand =
      Brand(brandId, this)
  }
}
