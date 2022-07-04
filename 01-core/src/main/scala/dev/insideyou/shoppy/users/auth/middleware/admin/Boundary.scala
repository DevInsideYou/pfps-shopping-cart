package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats._
import cats.syntax.all._

object BoundaryImpl {
  def make[F[_]: Monad: NonEmptyParallel, Auth, Token: Eq](
      gate: Gate[F, Auth, Token]
  ): Boundary[F, AdminUser, Auth, Token] =
    new Boundary[F, AdminUser, Auth, Token] {
      override lazy val authMiddleware: F[AuthMiddleware[F, AdminUser, Auth, Token]] =
        for {
          config                  <- gate.config
          (adminToken, adminAuth) <- tokenAndAuth(config)
          claim                   <- gate.claim(adminToken, adminAuth)
        } yield AuthMiddleware(
          adminAuth,
          find = token =>
            (token === adminToken)
              .guard[Option]
              .as(adminUser(claim))
              .pure
        )

      private def tokenAndAuth(config: Config): F[(Token, Auth)] =
        (gate.token(config.adminKey), gate.auth(config.tokenKey)).parTupled

      private def adminUser(content: ClaimContent): AdminUser =
        AdminUser(User(UserId(content.uuid), UserName("admin")))
    }
}
