package dev.insideyou
package shoppy

import cats.effect._
import cats.effect.std.Console
import cats.syntax.all._
import fs2.io.net.Network
import natchez.Trace.Implicits.noop
import skunk._

object StoragePostgresSession {
  def mkPostgreSqlResource[F[_]: Concurrent: Network: Console: CheckPostgresConnection](
      c: PostgreSQLConfig
  ): SessionPool[F] =
    Session
      .pooled[F](
        host = c.host.value,
        port = c.port.value,
        user = c.user.value,
        password = c.password.value.some,
        database = c.database.value,
        max = c.max.value
      )
      .evalTap(CheckPostgresConnection[F].checkPostgresConnection)
}
