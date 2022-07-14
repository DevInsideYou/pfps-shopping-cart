package dev.insideyou
package shoppy
package checkout

import scala.util.control.NoStackTrace

import derevo._
import derevo.cats.show

@derive(show)
case object EmptyCartError extends NoStackTrace
