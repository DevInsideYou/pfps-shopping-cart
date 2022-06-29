package dev.insideyou
package shoppy
package health

import cats._
import cats.effect._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands
import skunk.Session

object DI {
  def make[F[_]: Temporal: NonEmptyParallel](
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String]
  ): F[Controller[F]] =
    ControllerImpl
      .make(
        boundary = BoundaryImpl.make(
          gate = Gate.make(
            persistence = PersistenceImpl.make(postgres),
            redis = RedisImpl.make(redis)
          )
        )
      )
      .pure
}
