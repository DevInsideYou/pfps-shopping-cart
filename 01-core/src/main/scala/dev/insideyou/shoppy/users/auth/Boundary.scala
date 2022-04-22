package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._

trait Boundary[F[_]] {
  def newUser(username: UserName, password: Password): F[JwtToken]
  def login(username: UserName, password: Password): F[JwtToken]
  def logout(token: JwtToken, username: UserName): F[Unit]
}

object BoundaryImpl {
  def make[F[_]: MonadThrow](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      override def newUser(username: UserName, password: Password): F[JwtToken] =
        gate.findUser(username).flatMap {
          case Some(_) => UserNameInUse(username).raiseError
          case None    => create(username, password)
        }

      private def create(username: UserName, password: Password): F[JwtToken] =
        for {
          config <- gate.config
          userId <- createOrRaise(username, password)
          token  <- gate.createToken(config)
          _      <- gate.setUserInRedis(User(userId, username), token, config.tokenExpiration)
        } yield token

      private def createOrRaise(username: UserName, password: Password): F[UserId] =
        gate
          .createUser(username, gate.encrypt(password))
          .flatMap(_.fold(_.raiseError, _.pure))

      override def login(
          username: UserName,
          password: Password
      ): F[JwtToken] =
        for {
          config <- gate.config
          token <- gate.findUser(username).flatMap {
            case None =>
              UserNotFound(username).raiseError[F, JwtToken]

            case Some(user) if user.password =!= gate.encrypt(password) =>
              InvalidPassword(user.name).raiseError[F, JwtToken]

            case Some(userWithPassword) =>
              getOrElseCreateToken(username, userWithPassword, config)
          }
        } yield token

      private def getOrElseCreateToken(
          username: UserName,
          userWithPassword: UserWithPassword,
          config: Config
      ): F[JwtToken] =
        gate.getTokenFromRedis(username).flatMap {
          case Some(t) => t.pure[F]
          case None =>
            gate
              .createToken(config)
              .flatTap(gate.setUserWithPasswordInRedis(userWithPassword, config.tokenExpiration))

        }

      override def logout(token: JwtToken, username: UserName): F[Unit] =
        gate.deleteUserInRedis(username, token)
    }
}
