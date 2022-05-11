package dev.insideyou
package shoppy
package users
package auth
package middleware

import derevo.cats._
import derevo.derive
import io.estatico.newtype.macros.newtype

package object admin {
  @derive(show)
  @newtype
  case class AdminUser(value: User)
}
