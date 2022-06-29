package dev.insideyou
package shoppy
package users

import scala.concurrent.duration.FiniteDuration

import derevo.cats._
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.types.all._
import io.estatico.newtype.macros.newtype

package object auth {
  @derive(eqv, show)
  @newtype
  final case class EncryptedPassword(value: String)
  @derive(eqv, show)
  @newtype
  final case class Password(value: String)

  @newtype
  final case class TokenExpiration(value: FiniteDuration)

  @derive(show)
  @newtype
  final case class PasswordSalt(secret: NonEmptyString)

  @derive(show)
  @newtype
  final case class JwtAccessTokenKeyConfig(secret: NonEmptyString)

  @derive(eqv, show)
  @newtype
  final case class UserRepr(value: String)
}
