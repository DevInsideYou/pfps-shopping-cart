package dev.insideyou
package shoppy
package users
package auth

trait Gate[F[_], Token]
    extends HasConfig[F, Config]
    with Persistence[F]
    with Crypto
    with Tokens[F, Token]
    with Redis[F, Token]
    with ReprMaker[F]

object Gate {
  def make[F[_], Token](
      hasConfig: HasConfig[F, Config],
      persistence: Persistence[F],
      crypto: Crypto,
      tokens: Tokens[F, Token],
      redis: Redis[F, Token],
      reprMaker: ReprMaker[F]
  ): Gate[F, Token] =
    new Gate[F, Token] {
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
        tokens.createToken(config)

      override def cacheUserInRedis(
          userRepr: UserRepr,
          userName: UserName,
          token: Token,
          expiresIn: TokenExpiration
      ): F[Unit] =
        redis.cacheUserInRedis(userRepr, userName, token, expiresIn)

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

      override def makeUserRepr(user: User): F[UserRepr] =
        reprMaker.makeUserRepr(user)

    }
}

trait Persistence[F[_]] {
  def findUser(userName: UserName): F[Option[UserWithPassword]]
  def createUser(userName: UserName, password: EncryptedPassword): F[Either[UserNameInUse, UserId]]
}

trait Crypto {
  def encrypt(password: Password): EncryptedPassword
}

trait Tokens[F[_], Token] {
  def createToken(config: Config): F[Token]
}

trait Redis[F[_], Token] {
  def cacheUserInRedis(
      userRepr: UserRepr,
      userName: UserName,
      token: Token,
      expiresIn: TokenExpiration
  ): F[Unit]

  def cacheUserWithPasswordInRedis(
      userWithPassword: UserWithPassword,
      expiresIn: TokenExpiration
  )(
      token: Token
  ): F[Unit]

  def getTokenFromRedis(userName: UserName): F[Option[Token]]
  def deleteUserInRedis(userName: UserName, token: Token): F[Unit]
}

trait ReprMaker[F[_]] {
  def makeUserRepr(user: User): F[UserRepr]
}
