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

object DI {
  def make[F[_]: Async: GenUUID: JwtExpire: NonEmptyParallel](
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String],
      authMiddleware: AuthMiddleware[F, CommonUser]
  ): F[OpenController[F]] =
    for {
      hasConfig <- HasConfigImpl.make.pure[F]
      config    <- hasConfig.config
      crypto    <- CryptoImpl.make(config.passwordSalt)
    } yield {
      Controller.make(
        boundary = BoundaryImpl.make(
          gate = Gate.make(
            hasConfig = hasConfig,
            storage = StoragePostgresImpl.make(postgres),
            crypto = crypto,
            tokens = TokensImpl.make,
            redis = RedisImpl.make(redis),
            reprMaker = ReprMakerImpl.make
          )
        ),
        authMiddleware = authMiddleware
      )
    }
}
