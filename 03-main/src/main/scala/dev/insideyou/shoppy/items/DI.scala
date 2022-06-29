package dev.insideyou
package shoppy
package items

import cats.syntax.all._
import cats.effect._
import skunk.Session

object DI {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  )(implicit C: fs2.Compiler[F, F]): F[Controller[F]] =
    ControllerImpl
      .make(
        boundary = BoundaryImpl.make(
          gate = Gate.make(
            persistence = PersistenceImpl.make(postgres)
          )
        )
      )
      .pure
}
