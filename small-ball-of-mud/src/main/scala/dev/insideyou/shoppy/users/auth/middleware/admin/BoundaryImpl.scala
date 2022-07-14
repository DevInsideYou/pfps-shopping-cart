package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats._
import cats.syntax.all._

object BoundaryImpl {
  def make[F[_]: Monad: NonEmptyParallel, Authy, Token: Eq](
      dependencies: Dependencies[F, Authy, Token]
  ): Boundary[F, AdminUser, Authy, Token] =
    new Boundary[F, AdminUser, Authy, Token] {
      override lazy val authMiddleware: F[AuthMiddleware[F, AdminUser, Authy, Token]] =
        for {
          config                  <- dependencies.config
          (adminToken, adminAuth) <- tokenAndAuth(config)
          claim                   <- dependencies.claim(adminToken, adminAuth)
        } yield AuthMiddleware(
          adminAuth,
          find = token =>
            (token === adminToken)
              .guard[Option]
              .as(adminUser(claim))
              .pure
        )

      private def tokenAndAuth(config: Config): F[(Token, Authy)] =
        (dependencies.token(config.adminKey), dependencies.auth(config.tokenKey)).parTupled

      private def adminUser(content: ClaimContent): AdminUser =
        AdminUser(User(UserId(content.uuid), UserName("admin")))
    }

  trait Dependencies[F[_], Authy, Token] extends HasConfig[F, Config] with Auth[F, Authy, Token]

  def make[F[_]: Monad: NonEmptyParallel, Authy, Token: Eq](
      hasConfig: HasConfig[F, Config],
      _auth: Auth[F, Authy, Token]
  ): Boundary[F, AdminUser, Authy, Token] =
    make {
      new Dependencies[F, Authy, Token] {
        override def config: F[Config] =
          hasConfig.config

        override def token(adminKey: AdminUserTokenConfig): F[Token] =
          _auth.token(adminKey)

        override def auth(tokenKey: JwtSecretKeyConfig): F[Authy] =
          _auth.auth(tokenKey)

        override def claim(token: Token, auth: Authy): F[ClaimContent] =
          _auth.claim(token, auth)
      }
    }

}
