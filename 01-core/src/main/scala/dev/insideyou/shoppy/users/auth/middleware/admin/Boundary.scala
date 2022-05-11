package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats.Applicative
import cats.syntax.all._

object BoundaryImpl {
  def make[F[_]: Applicative](
      adminToken: JwtToken, // TODO move to the gate
      adminUser: AdminUser, // TODO move to the gate
      gate: Gate[F]
  ): Boundary[F, AdminUser] =
    new Boundary[F, AdminUser] {
      override lazy val authMiddleware: F[AuthMiddleware[F, AdminUser]] =
        gate.tokenKeyConfig.map { tokenKeyConfig =>
          AuthMiddleware(
            tokenKeyConfig,
            find = token =>
              (token === adminToken)
                .guard[Option]
                .as(adminUser)
                .pure
          )
        }
    }
}

trait Gate[F[_]] {
  def tokenKeyConfig: F[JwtAccessTokenKeyConfig]
}
