package dev.insideyou
package shoppy
package users
package auth
package middleware

import java.util.UUID

import scala.concurrent.duration.FiniteDuration

import derevo.cats._
import derevo.derive
import eu.timepit.refined.cats._
import eu.timepit.refined.types.all._
import io.estatico.newtype.macros.newtype

package object admin {
  @derive(show)
  @newtype
  case class AdminUser(value: User)
}
