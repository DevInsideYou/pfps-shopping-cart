package dev.insideyou
package shoppy
package users
package auth
package middleware

import derevo.cats._
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.types.all._
import io.estatico.newtype.macros.newtype
import java.util.UUID

package object admin {
  @derive(show)
  @newtype
  case class AdminUserTokenConfig(secret: NonEmptyString)

  @derive(show)
  @newtype
  case class JwtSecretKeyConfig(secret: NonEmptyString)

  @newtype
  case class ClaimContent(uuid: UUID)
}
