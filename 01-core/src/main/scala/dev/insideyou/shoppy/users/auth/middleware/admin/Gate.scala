package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

trait Gate[F[_], Auth, Token] extends HasConfig[F, Config] with Tokens[F, Auth, Token]

object Gate {
  def make[F[_], Auth, Token](
      hasConfig: HasConfig[F, Config],
      tokens: Tokens[F, Auth, Token]
  ): Gate[F, Auth, Token] =
    new Gate[F, Auth, Token] {
      override def config: F[Config] =
        hasConfig.config

      override def token(adminKey: AdminUserTokenConfig): F[Token] =
        tokens.token(adminKey)

      override def auth(tokenKey: JwtSecretKeyConfig): F[Auth] =
        tokens.auth(tokenKey)

      override def claim(token: Token, auth: Auth): F[ClaimContent] =
        tokens.claim(token, auth)
    }
}

trait Tokens[F[_], Auth, Token] {
  def token(adminKey: AdminUserTokenConfig): F[Token]
  def auth(tokenKey: JwtSecretKeyConfig): F[Auth]
  def claim(token: Token, auth: Auth): F[ClaimContent]
}
