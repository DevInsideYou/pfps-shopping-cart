package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

trait Auth[F[_], Authy, Token] {
  def token(adminKey: AdminUserTokenConfig): F[Token]
  def auth(tokenKey: JwtSecretKeyConfig): F[Authy]
  def claim(token: Token, auth: Authy): F[ClaimContent]
}
