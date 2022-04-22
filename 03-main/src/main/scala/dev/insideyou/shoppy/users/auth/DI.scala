package dev.insideyou
package shoppy
package users
package auth

import cats.syntax.all._
import cats.effect._
import skunk.Session

object DI {
  def make[F[_]: Async: GenUUID: JwtExpire](
      postgres: Resource[F, Session[F]]
  ): F[Controller[F]] =
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
            redis = null // TODO
          )
        )
      )
    }
}
