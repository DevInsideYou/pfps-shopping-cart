package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.effect._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands
import skunk.Session
import org.http4s.server.AuthMiddleware
import dev.profunktor._

object DI {
  def make[F[_]: Async: GenUUID: JwtExpire: NonEmptyParallel](
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String],
      authMiddleware: AuthMiddleware[F, CommonUser]
  ): F[Controller.Open[F]] =
    for {
      hasConfig <- HasConfigImpl.make.pure
      config    <- hasConfig.config
      crypto    <- CryptoImpl.make(config.passwordSalt)
    } yield {
      ControllerImpl.make(
        boundary = BoundaryImpl.make(
          gate = Gate.make(
            hasConfig = hasConfig,
            storage = StoragePostgresImpl.make(postgres),
            crypto = crypto,
            tokens = TokensImpl.make,
            redis = RedisImpl.make(redis, stringToToken = auth.jwt.JwtToken),
            reprMaker = ReprMakerImpl.make
          )
        ),
        authMiddleware = authMiddleware
      )
    }

  private implicit val ShowForJwtToken: Show[auth.jwt.JwtToken] =
    Show.fromToString
}
