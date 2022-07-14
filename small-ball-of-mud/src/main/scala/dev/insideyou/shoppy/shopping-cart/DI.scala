package dev.insideyou
package shoppy
package shopping_cart

import scala.util.chaining._

import cats._
import cats.syntax.all._
import cats.effect._
import dev.profunktor.redis4cats.RedisCommands
import org.http4s.server.AuthMiddleware
import skunk.Session

object DI {
  def make[F[_]: Async: NonEmptyParallel](
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String],
      authMiddleware: AuthMiddleware[F, CommonUser]
  ): F[(Controller[F], Boundary[F])] =
    BoundaryImpl
      .make(
        hasConfig = HasConfigImpl.make,
        persistence = PersistenceImpl.make(postgres),
        redis = RedisImpl.make(redis)
      )
      .pipe { boundary =>
        ControllerImpl.make(authMiddleware, boundary) -> boundary
      }
      .pure
}
