package dev.insideyou
package shoppy

import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype

object Redis {
  @newtype case class Config(uri: Config.URI)

  object Config {
    @newtype case class URI(value: NonEmptyString)
  }
}
