package dev.insideyou
package shoppy
package users
package auth
package middleware

trait Gate[F[_]] extends Redis[F] with ReprMaker[F] with Tokens[F]

object Gate {
  def make[F[_]](redis: Redis[F], reprMaker: ReprMaker[F], tokens: Tokens[F]): Gate[F] =
    new Gate[F] {
      override def getUserStringFromCache(token: JwtToken): F[Option[String]] =
        redis.getUserStringFromCache(token)

      override def convertToCommonUser(userString: String): F[Option[CommonUser]] =
        reprMaker.convertToCommonUser(userString)

      override def tokenKeyConfig: F[JwtAccessTokenKeyConfig] =
        tokens.tokenKeyConfig
    }
}

trait Redis[F[_]] {
  def getUserStringFromCache(token: JwtToken): F[Option[String]]
}

trait ReprMaker[F[_]] {
  def convertToCommonUser(userString: String): F[Option[CommonUser]]
}

trait Tokens[F[_]] {
  def tokenKeyConfig: F[JwtAccessTokenKeyConfig]
}
