package dev.insideyou
package shoppy
package users
package auth

trait Persistence[F[_]] {
  def findUser(userName: UserName): F[Option[UserWithPassword]]
  def createUser(userName: UserName, password: EncryptedPassword): F[Either[UserNameInUse, UserId]]
}

trait Crypto {
  def encrypt(password: Password): EncryptedPassword
}

trait Auth[F[_], Token] {
  def createToken(config: Config): F[Token]
}

trait Redis[F[_], Token] {
  def cacheUserInRedis(
      user: User,
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
