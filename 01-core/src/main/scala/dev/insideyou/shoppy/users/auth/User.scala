package dev.insideyou
package shoppy
package users
package auth

import derevo.cats._
import derevo.derive

@derive(show)
final case class User(id: UserId, name: UserName)
