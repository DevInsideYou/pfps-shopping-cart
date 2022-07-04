package dev.insideyou
package shoppy
package users
package auth
package middleware

trait Gate[F[_], Auth, Token] extends HasConfig[F, Config] with Redis[F, Token] with Tokens[F, Auth]

object Gate {
  def make[F[_], Auth, Token](
      hasConfig: HasConfig[F, Config],
      redis: Redis[F, Token],
      tokens: Tokens[F, Auth]
  ): Gate[F, Auth, Token] =
    new Gate[F, Auth, Token] {
      override def config: F[Config] =
        hasConfig.config

      override def getUserFromCache(token: Token): F[Option[User]] =
        redis.getUserFromCache(token)

      override def auth(tokenKey: JwtAccessTokenKeyConfig): F[Auth] =
        tokens.auth(tokenKey)
    }
}

trait Redis[F[_], Token] {
  def getUserFromCache(token: Token): F[Option[User]]
}

trait Tokens[F[_], Auth] {
  def auth(tokenKey: JwtAccessTokenKeyConfig): F[Auth]
}
