package dev.insideyou
package shoppy
package users
package auth
package middleware

trait Gate[F[_], Auth, Token]
    extends HasConfig[F, Config]
    with Redis[F, Token]
    with ReprMaker[F]
    with Tokens[F, Auth]

object Gate {
  def make[F[_], Auth, Token](
      hasConfig: HasConfig[F, Config],
      redis: Redis[F, Token],
      reprMaker: ReprMaker[F],
      tokens: Tokens[F, Auth]
  ): Gate[F, Auth, Token] =
    new Gate[F, Auth, Token] {
      override def config: F[Config] =
        hasConfig.config

      override def getUserStringFromCache(token: Token): F[Option[String]] =
        redis.getUserStringFromCache(token)

      override def convertToCommonUser(userString: String): F[Option[CommonUser]] =
        reprMaker.convertToCommonUser(userString)

      override def auth(tokenKey: JwtAccessTokenKeyConfig): F[Auth] =
        tokens.auth(tokenKey)
    }
}

trait Redis[F[_], Token] {
  def getUserStringFromCache(token: Token): F[Option[String]]
}

trait ReprMaker[F[_]] {
  def convertToCommonUser(userString: String): F[Option[CommonUser]]
}

trait Tokens[F[_], Auth] {
  def auth(tokenKey: JwtAccessTokenKeyConfig): F[Auth]
}
