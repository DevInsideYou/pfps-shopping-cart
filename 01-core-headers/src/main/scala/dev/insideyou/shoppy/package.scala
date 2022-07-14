package dev.insideyou

import java.util.UUID

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype

package object shoppy {
  @derive(show)
  @newtype
  final case class CommonUser(value: User)

  @derive(show)
  @newtype
  case class AdminUser(value: User)

  @derive(eqv, show, uuid)
  @newtype
  final case class UserId(value: UUID)

  @derive(eqv, show)
  @newtype
  final case class UserName(value: String)
}
