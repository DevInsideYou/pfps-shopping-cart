package dev.insideyou
package shoppy
package categories

import derevo.cats._
import derevo.derive

@derive(eqv, show)
final case class Category(uuid: CategoryId, name: CategoryName)
