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
  def make[F[_]: Async: NonEmptyParallel]: Middleware[F, AdminUser] =
    MiddlewareImpl.make(
      boundary = BoundaryImpl.make(
        hasConfig = HasConfigImpl.make,
        _auth = AuthImpl.make
      )
    )

  private implicit lazy val EqForJwtToken: Eq[jwt.JwtToken] =
    Eq.fromUniversalEquals
}
