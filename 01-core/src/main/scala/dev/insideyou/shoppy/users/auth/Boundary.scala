package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._

trait Auth[F[_]] {
  def newUser(username: UserName, password: Password): F[JwtToken]
  def login(username: UserName, password: Password): F[JwtToken]
  def logout(token: JwtToken, username: UserName): F[Unit]
}

object AuthImpl {
  def make[F[_]: MonadThrow](gate: Gate[F]): Auth[F] =
    new Auth[F] {
      override def newUser(username: UserName, password: Password): F[JwtToken] =
        gate.findUser(username).flatMap {
          case Some(_) => UserNameInUse(username).raiseError
          case None    => create(username, password)
        }

      private def create(username: UserName, password: Password): F[JwtToken] =
        for {
          userId          <- createOrRaise(username, password)
          token           <- gate.createToken
          tokenExpiration <- getTokenExpiration
          _               <- gate.setUserInRedis(User(userId, username), token, tokenExpiration)
        } yield token

      private def createOrRaise(username: UserName, password: Password): F[UserId] =
        gate
          .createUser(username, gate.encrypt(password))
          .flatMap(_.fold(_.raiseError, _.pure))

      override def login(username: UserName, password: Password): F[JwtToken] =
        for {
          tokenExpiration <- getTokenExpiration
          token <- gate.findUser(username).flatMap {
            case None =>
              UserNotFound(username).raiseError[F, JwtToken]

            case Some(user) if user.password =!= gate.encrypt(password) =>
              InvalidPassword(user.name).raiseError[F, JwtToken]

            case Some(userWithPassword) =>
              getOrElseCreateToken(username, userWithPassword, tokenExpiration)
          }
        } yield token

      private def getOrElseCreateToken(
          username: UserName,
          userWithPassword: UserWithPassword,
          tokenExpiration: TokenExpiration
      ): F[JwtToken] =
        gate.getTokenFromRedis(username).flatMap {
          case Some(t) => t.pure[F]
          case None =>
            gate.createToken
              .flatTap(gate.setUserWithPasswordInRedis(userWithPassword, tokenExpiration))

        }

      override def logout(token: JwtToken, username: UserName): F[Unit] =
        gate.deleteUserInRedis(username, token)

      private lazy val getTokenExpiration =
        gate.config.map(_.tokenExpiration)
    }
}
