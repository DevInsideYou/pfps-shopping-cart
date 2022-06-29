package dev.insideyou
package shoppy

import enumeratum._

sealed abstract class AppEnvironment extends EnumEntry with EnumEntry.Lowercase

object AppEnvironment extends Enum[AppEnvironment] with CirisEnum[AppEnvironment] {
  case object Test extends AppEnvironment
  case object Prod extends AppEnvironment

  lazy val values = findValues
}
