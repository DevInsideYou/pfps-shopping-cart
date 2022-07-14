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

object AuthImpl {
  def make[F[_]: GenUUID: Monad](jwtExpire: JwtExpire[F]): Auth[F, auth.jwt.JwtToken] =
    new Auth[F, auth.jwt.JwtToken] {
      override def createToken(config: Config): F[auth.jwt.JwtToken] =
        for {
          uuid  <- GenUUID[F].make
          claim <- expireClaim(uuid, config.tokenExpiration)
          secretKey = auth.jwt.JwtSecretKey(config.jwtAccessTokenKeyConfig.secret.value)
          token <- auth.jwt.jwtEncode[F](claim, secretKey, pdi.jwt.JwtAlgorithm.HS256)
        } yield token

      private def expireClaim(uuid: UUID, tokenExpiration: TokenExpiration): F[pdi.jwt.JwtClaim] =
        jwtExpire.expiresIn(pdi.jwt.JwtClaim(uuid.asJson.noSpaces), tokenExpiration)
    }
}
