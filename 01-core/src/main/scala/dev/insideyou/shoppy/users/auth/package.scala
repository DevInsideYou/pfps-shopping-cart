package dev.insideyou
package shoppy
package users

import scala.concurrent.duration.FiniteDuration

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype
import java.util.UUID

package object auth {
  @derive(eqv, show)
  @newtype
  final case class UserName(value: String)

  @derive(eqv, show)
  @newtype
  final case class EncryptedPassword(value: String)

  @derive(eqv, show, uuid)
  @newtype
  final case class UserId(value: UUID)

  @derive(eqv, show)
  @newtype
  final case class Password(value: String)

  @newtype
  final case class TokenExpiration(value: FiniteDuration)
}
