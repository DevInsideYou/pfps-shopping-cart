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
    with ReprMaker[F]

object Gate {
  def make[F[_]](
      hasConfig: HasConfig[F],
      storage: Storage[F],
      crypto: Crypto,
      tokens: Tokens[F],
      redis: Redis[F],
      reprMaker: ReprMaker[F]
  ): Gate[F] =
    new Gate[F] {
      override def config: F[Config] =
        hasConfig.config

      override def findUser(userName: UserName): F[Option[UserWithPassword]] =
        storage.findUser(userName)

      override def createUser(
          userName: UserName,
          password: EncryptedPassword
      ): F[Either[UserNameInUse, UserId]] =
        storage.createUser(userName, password)

      override def encrypt(password: Password): EncryptedPassword =
        crypto.encrypt(password)

      override def createToken(config: Config): F[JwtToken] =
        tokens.createToken(config)

      override def cacheUserInRedis(
          userRepr: UserRepr,
          userName: UserName,
          token: JwtToken,
          expiresIn: TokenExpiration
      ): F[Unit] =
        redis.cacheUserInRedis(userRepr, userName, token, expiresIn)

      override def cacheUserWithPasswordInRedis(
          userWithPassword: UserWithPassword,
          expiresIn: TokenExpiration
      )(
          token: JwtToken
      ): F[Unit] =
        redis.cacheUserWithPasswordInRedis(userWithPassword, expiresIn)(token)

      override def getTokenFromRedis(userName: UserName): F[Option[JwtToken]] =
        redis.getTokenFromRedis(userName)

      override def deleteUserInRedis(userName: UserName, token: JwtToken): F[Unit] =
        redis.deleteUserInRedis(userName, token)

      override def makeUserRepr(user: User): F[UserRepr] =
        reprMaker.makeUserRepr(user)

    }
}

trait HasConfig[F[_]] {
  def config: F[Config]
}

trait Storage[F[_]] {
  def findUser(userName: UserName): F[Option[UserWithPassword]]
  def createUser(userName: UserName, password: EncryptedPassword): F[Either[UserNameInUse, UserId]]
}

trait Crypto {
  def encrypt(password: Password): EncryptedPassword
}

trait Tokens[F[_]] {
  def createToken(config: Config): F[JwtToken]
}

trait Redis[F[_]] {
  def cacheUserInRedis(
      userRepr: UserRepr,
      userName: UserName,
      token: JwtToken,
      expiresIn: TokenExpiration
  ): F[Unit]

  def cacheUserWithPasswordInRedis(
      userWithPassword: UserWithPassword,
      expiresIn: TokenExpiration
  )(
      token: JwtToken
  ): F[Unit]

  def getTokenFromRedis(userName: UserName): F[Option[JwtToken]]
  def deleteUserInRedis(userName: UserName, token: JwtToken): F[Unit]
}

trait ReprMaker[F[_]] {
  def makeUserRepr(user: User): F[UserRepr]
}
