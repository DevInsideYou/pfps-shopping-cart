package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats._
import cats.syntax.all._

object BoundaryImpl {
  def make[F[_]: Monad, Auth, Token: Eq](
      adminToken: Token,    // TODO move to the gate
      adminUser: AdminUser, // TODO move to the gate
      gate: Gate[F, Auth]
  ): Boundary[F, AdminUser, Auth, Token] =
    new Boundary[F, AdminUser, Auth, Token] {
      override lazy val authMiddleware: F[AuthMiddleware[F, AdminUser, Auth, Token]] =
        gate.tokenKeyConfig.flatMap(gate.auth).map { auth =>
          AuthMiddleware(
            auth,
            find = token =>
              (token === adminToken)
                .guard[Option]
                .as(adminUser)
                .pure
          )
        }
    }
}

trait Gate[F[_], Auth] {
  def auth(tokenKeyConfig: JwtAccessTokenKeyConfig): F[Auth]
  def tokenKeyConfig: F[JwtAccessTokenKeyConfig]
}
