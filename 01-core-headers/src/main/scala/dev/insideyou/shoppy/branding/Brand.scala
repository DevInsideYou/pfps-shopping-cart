package dev.insideyou
package shoppy
package branding

import derevo.cats._
import derevo.derive

@derive(eqv, show)
final case class Brand(uuid: BrandId, name: BrandName)
