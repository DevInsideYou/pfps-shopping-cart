package dev.insideyou
package shoppy
package users
package auth

trait Gate[F[_]] extends HasConfig[F] with Storage[F] with Crypto with Tokens[F] with Redis[F]

object Gate {
  def make[F[_]](
      hasConfig: HasConfig[F],
      storage: Storage[F],
      crypto: Crypto,
      tokens: Tokens[F],
      redis: Redis[F]
  ): Gate[F] =
    new Gate[F] {
      def config: F[Config] =
        hasConfig.config

      override def findUser(username: UserName): F[Option[UserWithPassword]] =
        storage.findUser(username)

      override def createUser(
          username: UserName,
          password: EncryptedPassword
      ): F[Either[UserNameInUse, UserId]] =
        storage.createUser(username, password)

      def encrypt(value: Password): EncryptedPassword =
        crypto.encrypt(value)

      lazy val createToken: F[JwtToken] =
        tokens.createToken

      def setUserInRedis(user: User, token: JwtToken, expiresIn: TokenExpiration): F[Unit] =
        redis.setUserInRedis(user, token, expiresIn)

      def setUserWithPasswordInRedis(
          user: UserWithPassword,
          expiresIn: TokenExpiration
      )(
          token: JwtToken
      ): F[Unit] =
        redis.setUserWithPasswordInRedis(user, expiresIn)(token)

      def getTokenFromRedis(username: UserName): F[Option[JwtToken]] =
        redis.getTokenFromRedis(username)

      def deleteUserInRedis(username: UserName, token: JwtToken): F[Unit] =
        redis.deleteUserInRedis(username, token)
    }
}

trait HasConfig[F[_]] {
  def config: F[Config]
}

trait Storage[F[_]] {
  def findUser(username: UserName): F[Option[UserWithPassword]]
  def createUser(username: UserName, password: EncryptedPassword): F[Either[UserNameInUse, UserId]]
}

trait Crypto {
  def encrypt(password: Password): EncryptedPassword
}

trait Tokens[F[_]] {
  def createToken: F[JwtToken]
}

trait Redis[F[_]] {
  def setUserInRedis(user: User, token: JwtToken, expiresIn: TokenExpiration): F[Unit]

  def setUserWithPasswordInRedis(
      user: UserWithPassword,
      expiresIn: TokenExpiration
  )(
      token: JwtToken
  ): F[Unit]

  def getTokenFromRedis(username: UserName): F[Option[JwtToken]]
  def deleteUserInRedis(username: UserName, token: JwtToken): F[Unit]
}
