package dev.insideyou
package shoppy
package checkout

import scala.util.control.NoStackTrace

import derevo._
import derevo.cats.show

@derive(show)
final case class CartNotFound(userId: UserId) extends NoStackTrace
