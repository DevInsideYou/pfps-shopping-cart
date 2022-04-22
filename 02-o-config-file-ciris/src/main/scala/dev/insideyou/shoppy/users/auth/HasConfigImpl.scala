package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._
import scala.concurrent.duration._

object HasConfigImpl {
  def make[F[_]: Applicative]: HasConfig[F] =
    new HasConfig[F] {
      override lazy val config: F[Config] =
        Config(TokenExpiration(30.minutes)).pure[F]
    }
}
