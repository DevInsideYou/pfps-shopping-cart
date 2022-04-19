package dev.insideyou
package shoppy

import java.util.UUID

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype

package object users {
  @derive(eqv, show)
  @newtype
  case class UserName(value: String)

  @derive(eqv, show)
  @newtype
  case class EncryptedPassword(value: String)

  @derive(eqv, show, uuid)
  @newtype
  case class UserId(value: UUID)
}
