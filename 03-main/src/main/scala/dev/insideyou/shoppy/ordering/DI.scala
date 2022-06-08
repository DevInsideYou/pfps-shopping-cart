package dev.insideyou
package shoppy
package ordering

import cats.effect._
import cats.syntax.all._
import org.http4s.server.AuthMiddleware
import skunk.Session

object DI {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]],
      authMiddleware: AuthMiddleware[F, CommonUser]
  )(implicit C: fs2.Compiler[F, F]): F[Controller[F]] =
    ControllerImpl
      .make(
        boundary = BoundaryImpl.make(
          gate = Gate.make(
            storage = StoragePostgresImpl.make(postgres)
          )
        ),
        authMiddleware = authMiddleware
      )
      .pure
}