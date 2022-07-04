package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._

trait Boundary[F[_], Token] {
  def newUser(userName: UserName, password: Password): F[Token]
  def login(userName: UserName, password: Password): F[Token]
  def logout(token: Token, userName: UserName): F[Unit]
}

object BoundaryImpl {
  def make[F[_]: MonadThrow, Token](gate: Gate[F, Token]): Boundary[F, Token] =
    new Boundary[F, Token] {
      override def newUser(userName: UserName, password: Password): F[Token] =
        gate.findUser(userName).flatMap {
          case Some(_) => UserNameInUse(userName).raiseError
          case None    => create(userName, password)
        }

      private def create(userName: UserName, password: Password): F[Token] =
        for {
          config <- gate.config
          userId <- createOrRaiseIfInUse(userName, password)
          token  <- gate.createToken(config)
          user = User(userId, userName)
          _ <- gate.cacheUserInRedis(user, userName, token, config.tokenExpiration)
        } yield token

      private def createOrRaiseIfInUse(userName: UserName, password: Password): F[UserId] =
        gate
          .createUser(userName, gate.encrypt(password))
          .flatMap(_.fold(_.raiseError, _.pure))

      override def login(
          userName: UserName,
          password: Password
      ): F[Token] =
        for {
          config <- gate.config
          token <- gate.findUser(userName).flatMap {
            case None =>
              UserNotFound(userName).raiseError[F, Token]

            case Some(user) if user.password =!= gate.encrypt(password) =>
              InvalidPassword(user.name).raiseError[F, Token]

            case Some(userWithPassword) =>
              getOrElseCreateToken(userWithPassword, config)
          }
        } yield token

      private def getOrElseCreateToken(
          userWithPassword: UserWithPassword,
          config: Config
      ): F[Token] =
        gate.getTokenFromRedis(userWithPassword.name).flatMap {
          case Some(t) => t.pure
          case None =>
            gate
              .createToken(config)
              .flatTap(gate.cacheUserWithPasswordInRedis(userWithPassword, config.tokenExpiration))
        }

      override def logout(token: Token, userName: UserName): F[Unit] =
        gate.deleteUserInRedis(userName, token)
    }
}
