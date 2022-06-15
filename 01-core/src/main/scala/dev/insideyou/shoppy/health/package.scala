package dev.insideyou
package shoppy

import io.estatico.newtype.macros.newtype
import derevo.cats.eqv
import derevo.derive
import monocle.Iso

object health_package_object {
  @newtype
  final case class RedisStatus(value: Status)

  @newtype
  final case class PostgresStatus(value: Status)

  final case class AppStatus(
      redis: RedisStatus,
      postgres: PostgresStatus
  )

  @derive(eqv)
  sealed abstract class Status extends Product with Serializable
  object Status {
    case object Okay        extends Status
    case object Unreachable extends Status

    lazy val Bool: Iso[Status, Boolean] =
      Iso[Status, Boolean] {
        case Okay        => true
        case Unreachable => false
      }(if (_) Okay else Unreachable)
  }
}
