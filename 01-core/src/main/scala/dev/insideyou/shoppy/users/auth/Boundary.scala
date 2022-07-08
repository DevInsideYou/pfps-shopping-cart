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
  def make[F[_]: MonadThrow, Token](dependencies: Dependencies[F, Token]): Boundary[F, Token] =
    new Boundary[F, Token] {
      override def newUser(userName: UserName, password: Password): F[Token] =
        dependencies.findUser(userName).flatMap {
          case Some(_) => UserNameInUse(userName).raiseError
          case None    => create(userName, password)
        }

      private def create(userName: UserName, password: Password): F[Token] =
        for {
          config <- dependencies.config
          userId <- createOrRaiseIfInUse(userName, password)
          token  <- dependencies.createToken(config)
          user = User(userId, userName)
          _ <- dependencies.cacheUserInRedis(user, userName, token, config.tokenExpiration)
        } yield token

      private def createOrRaiseIfInUse(userName: UserName, password: Password): F[UserId] =
        dependencies
          .createUser(userName, dependencies.encrypt(password))
          .flatMap(_.fold(_.raiseError, _.pure))

      override def login(
          userName: UserName,
          password: Password
      ): F[Token] =
        for {
          config <- dependencies.config
          token <- dependencies.findUser(userName).flatMap {
            case None =>
              UserNotFound(userName).raiseError[F, Token]

            case Some(user) if user.password =!= dependencies.encrypt(password) =>
              InvalidPassword(user.name).raiseError[F, Token]

            case Some(userWithPassword) =>
              getOrElseCreateToken(userWithPassword, config)
          }
        } yield token

      private def getOrElseCreateToken(
          userWithPassword: UserWithPassword,
          config: Config
      ): F[Token] =
        dependencies.getTokenFromRedis(userWithPassword.name).flatMap {
          case Some(t) => t.pure
          case None =>
            dependencies
              .createToken(config)
              .flatTap(
                dependencies.cacheUserWithPasswordInRedis(userWithPassword, config.tokenExpiration)
              )
        }

      override def logout(token: Token, userName: UserName): F[Unit] =
        dependencies.deleteUserInRedis(userName, token)
    }

  trait Dependencies[F[_], Token]
      extends HasConfig[F, Config]
      with Persistence[F]
      with Crypto
      with Auth[F, Token]
      with Redis[F, Token]

  def make[F[_]: MonadThrow, Token](
      hasConfig: HasConfig[F, Config],
      persistence: Persistence[F],
      crypto: Crypto,
      auth: Auth[F, Token],
      redis: Redis[F, Token]
  ): Boundary[F, Token] =
    make {
      new Dependencies[F, Token] {
        override def config: F[Config] =
          hasConfig.config

        override def findUser(userName: UserName): F[Option[UserWithPassword]] =
          persistence.findUser(userName)

        override def createUser(
            userName: UserName,
            password: EncryptedPassword
        ): F[Either[UserNameInUse, UserId]] =
          persistence.createUser(userName, password)

        override def encrypt(password: Password): EncryptedPassword =
          crypto.encrypt(password)

        override def createToken(config: Config): F[Token] =
          auth.createToken(config)

        override def cacheUserInRedis(
            user: User,
            userName: UserName,
            token: Token,
            expiresIn: TokenExpiration
        ): F[Unit] =
          redis.cacheUserInRedis(user, userName, token, expiresIn)

        override def cacheUserWithPasswordInRedis(
            userWithPassword: UserWithPassword,
            expiresIn: TokenExpiration
        )(
            token: Token
        ): F[Unit] =
          redis.cacheUserWithPasswordInRedis(userWithPassword, expiresIn)(token)

        override def getTokenFromRedis(userName: UserName): F[Option[Token]] =
          redis.getTokenFromRedis(userName)

        override def deleteUserInRedis(userName: UserName, token: Token): F[Unit] =
          redis.deleteUserInRedis(userName, token)
      }
    }

}
