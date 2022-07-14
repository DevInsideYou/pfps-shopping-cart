package dev.insideyou
package shoppy
package users
package auth

trait Boundary[F[_], Token] {
  def newUser(userName: UserName, password: Password): F[Token]
  def login(userName: UserName, password: Password): F[Token]
  def logout(token: Token, userName: UserName): F[Unit]
}
