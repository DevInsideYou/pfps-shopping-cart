package dev.insideyou
package shoppy

import derevo.cats._
import derevo.derive

@derive(show)
final case class User(id: UserId, name: UserName)
