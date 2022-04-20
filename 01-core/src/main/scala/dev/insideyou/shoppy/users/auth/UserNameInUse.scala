package dev.insideyou
package shoppy
package users
package auth

import scala.util.control.NoStackTrace

final case class UserNameInUse(username: UserName) extends NoStackTrace
