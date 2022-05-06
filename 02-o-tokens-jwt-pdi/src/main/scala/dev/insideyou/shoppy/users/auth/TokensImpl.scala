package dev.insideyou
package shoppy
package users
package auth

import java.util.UUID

import cats._
import cats.syntax.all._
import dev.profunktor._
import eu.timepit.refined.auto._
import io.circe.syntax._

object TokensImpl {
  def make[F[_]: GenUUID: JwtExpire: Monad]: Tokens[F] =
    new Tokens[F] {
      override def createToken(config: Config): F[JwtToken] =
        for {
          uuid  <- GenUUID[F].make
          claim <- expireClaim(uuid, config.tokenExpiration)
          secretKey = auth.jwt.JwtSecretKey(config.jwtAccessTokenKeyConfig.secret.value)
          token <- auth.jwt.jwtEncode[F](claim, secretKey, pdi.jwt.JwtAlgorithm.HS256)
        } yield JwtToken(token.value)

      private def expireClaim(uuid: UUID, tokenExpiration: TokenExpiration): F[pdi.jwt.JwtClaim] =
        JwtExpire[F].expiresIn(pdi.jwt.JwtClaim(uuid.asJson.noSpaces), tokenExpiration)
    }
}
