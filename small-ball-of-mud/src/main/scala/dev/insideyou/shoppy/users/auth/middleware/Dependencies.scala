package dev.insideyou
package shoppy
package users
package auth
package middleware

trait Redis[F[_], Token] {
  def getUserFromCache(token: Token): F[Option[User]]
}

trait Auth[F[_], Authy] {
  def auth(tokenKey: JwtAccessTokenKeyConfig): F[Authy]
}
