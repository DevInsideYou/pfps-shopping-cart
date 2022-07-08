package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.syntax.all._

trait Boundary[F[_], A, Authy, Token] {
  def authMiddleware: F[AuthMiddleware[F, A, Authy, Token]]
}

object BoundaryImpl {
  def make[F[_]: Monad, Authy, Token](
      dependencies: Dependencies[F, Authy, Token]
  ): Boundary[F, CommonUser, Authy, Token] =
    new Boundary[F, CommonUser, Authy, Token] {
      override lazy val authMiddleware: F[AuthMiddleware[F, CommonUser, Authy, Token]] =
        dependencies.config.map(_.tokenKey).flatMap(dependencies.auth).map { auth =>
          AuthMiddleware(
            auth,
            find = token =>
              dependencies
                .getUserFromCache(token)
                .nested
                .map(CommonUser.apply)
                .value
          )
        }
    }

  trait Dependencies[F[_], Authy, Token]
      extends HasConfig[F, Config]
      with Redis[F, Token]
      with Auth[F, Authy]

  def make[F[_]: Monad, Authy, Token](
      hasConfig: HasConfig[F, Config],
      redis: Redis[F, Token],
      _auth: Auth[F, Authy]
  ): Boundary[F, CommonUser, Authy, Token] =
    make {
      new Dependencies[F, Authy, Token] {
        override def config: F[Config] =
          hasConfig.config

        override def getUserFromCache(token: Token): F[Option[User]] =
          redis.getUserFromCache(token)

        override def auth(tokenKey: JwtAccessTokenKeyConfig): F[Authy] =
          _auth.auth(tokenKey)
      }
    }

}
