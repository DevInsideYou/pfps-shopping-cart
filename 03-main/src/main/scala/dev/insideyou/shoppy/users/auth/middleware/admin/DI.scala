package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats._
import cats.effect._
import dev.profunktor.auth.jwt

object DI {
  def make[F[_]: Async: NonEmptyParallel]: Controller.Middleware[F, AdminUser] =
    ControllerImpl.make(
      boundary = BoundaryImpl.make(
        gate = Gate.make(
          hasConfig = HasConfigImpl.make,
          reprMaker = ReprMakerImpl.make,
          tokens = TokensImpl.make
        )
      )
    )

  private implicit lazy val EqForJwtToken: Eq[jwt.JwtToken] =
    Eq.fromUniversalEquals
}
