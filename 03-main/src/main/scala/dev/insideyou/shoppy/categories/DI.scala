package dev.insideyou
package shoppy
package categories

import cats.syntax.all._
import cats.effect._
import skunk.Session

object DI {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): F[Controller[F]] =
    ControllerImpl
      .make(
        boundary = BoundaryImpl.make(
          persistence = PersistenceImpl.make(postgres)
        )
      )
      .pure
}
