package dev.insideyou
package shoppy
package users
package auth

trait Gate[F[_]]
    extends HasConfig[F]
    with Storage[F]
    with Crypto
    with Tokens[F]
    with Redis[F]
    with KeyMaker[F]

object Gate {
  def make[F[_]](
      hasConfig: HasConfig[F],
      storage: Storage[F],
      crypto: Crypto,
      tokens: Tokens[F],
      redis: Redis[F],
      keyMaker: KeyMaker[F]
  ): Gate[F] =
    new Gate[F] {
      override def config: F[Config] =
        hasConfig.config

      override def findUser(username: UserName): F[Option[UserWithPassword]] =
        storage.findUser(username)

      override def createUser(
          username: UserName,
          password: EncryptedPassword
      ): F[Either[UserNameInUse, UserId]] =
        storage.createUser(username, password)

      override def encrypt(password: Password): EncryptedPassword =
        crypto.encrypt(password)

      override def createToken(config: Config): F[JwtToken] =
        tokens.createToken(config)

      override def setUserInRedis(
          user: String,
          token: JwtToken,
          expiresIn: TokenExpiration
      ): F[Unit] =
        redis.setUserInRedis(user, token, expiresIn)

      override def setUserWithPasswordInRedis(
          user: UserWithPassword,
          expiresIn: TokenExpiration
      )(
          token: JwtToken
      ): F[Unit] =
        redis.setUserWithPasswordInRedis(user, expiresIn)(token)

      override def getTokenFromRedis(username: UserName): F[Option[JwtToken]] =
        redis.getTokenFromRedis(username)

      override def deleteUserInRedis(username: UserName, token: JwtToken): F[Unit] =
        redis.deleteUserInRedis(username, token)

      override def makeUserKey(user: User): F[String] =
        keyMaker.makeUserKey(user)

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
  def createToken(config: Config): F[JwtToken]
}

trait Redis[F[_]] {
  // TODO use newtype instead of String (maybe even NonEmptyString)
  def setUserInRedis(user: String, token: JwtToken, expiresIn: TokenExpiration): F[Unit]

  def setUserWithPasswordInRedis(
      user: UserWithPassword,
      expiresIn: TokenExpiration
  )(
      token: JwtToken
  ): F[Unit]

  def getTokenFromRedis(username: UserName): F[Option[JwtToken]]
  def deleteUserInRedis(username: UserName, token: JwtToken): F[Unit]
}

trait KeyMaker[F[_]] {
  def makeUserKey(user: User): F[String]
}
