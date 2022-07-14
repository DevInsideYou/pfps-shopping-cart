package dev.insideyou
package shoppy

import eu.timepit.refined.types.all._

final case class PostgreSQLConfig(
    host: NonEmptyString,
    port: UserPortNumber,
    user: NonEmptyString,
    password: NonEmptyString,
    database: NonEmptyString,
    max: PosInt
)
