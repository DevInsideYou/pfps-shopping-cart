package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.syntax.all._
import dev.profunktor.auth.jwt
import eu.timepit.refined.auto._
import io.circe.syntax._
import pdi.jwt._

// TODO: remove dependency on json
object TokensImpl {
  def make[F[_]: GenUUID: JwtExpire: Monad]: Tokens[F] =
    new Tokens[F] {
      override def createToken(config: Config): F[JwtToken] =
        for {
          uuid  <- GenUUID[F].make
          claim <- JwtExpire[F].expiresIn(JwtClaim(uuid.asJson.noSpaces), config.tokenExpiration)
          secretKey = jwt.JwtSecretKey(config.jwtAccessTokenKeyConfig.secret.value)
          token <- jwt.jwtEncode[F](claim, secretKey, JwtAlgorithm.HS256)
        } yield JwtToken(token.value)
    }
}
