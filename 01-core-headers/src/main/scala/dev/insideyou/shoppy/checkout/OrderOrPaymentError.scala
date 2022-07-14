package dev.insideyou
package shoppy
package checkout

import scala.util.control.NoStackTrace
import derevo.derive
import derevo.cats._

@derive(show)
sealed abstract class Error extends NoStackTrace with Product with Serializable {
  def cause: String
}

object Error {
  @derive(eqv, show)
  final case class Order(cause: String) extends Error

  @derive(eqv, show)
  final case class Payment(cause: String) extends Error
}
