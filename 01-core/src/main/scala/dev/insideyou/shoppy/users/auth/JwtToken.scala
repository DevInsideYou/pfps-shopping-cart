package dev.insideyou
package shoppy
package users
package auth

import derevo.cats._
import derevo.derive

@derive(eqv, show)
final case class JwtToken(value: String) extends AnyVal
