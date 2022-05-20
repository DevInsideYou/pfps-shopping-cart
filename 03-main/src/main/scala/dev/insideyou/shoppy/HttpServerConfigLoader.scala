package dev.insideyou
package shoppy

import cats._
import cats.syntax.all._
import com.comcast.ip4s._

object HttpServerConfigLoader {
  def load[F[_]: Applicative]: F[HttpServerConfig] =
    HttpServerConfig(
      host = host"0.0.0.0",
      port = port"8080"
    ).pure
}
