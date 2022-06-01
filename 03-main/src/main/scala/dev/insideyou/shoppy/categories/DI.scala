package dev.insideyou
package shoppy
package categories

import cats.effect._
import skunk.Session

object DI {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Controller.Open[F] =
    ControllerImpl.make(
      boundary = BoundaryImpl.make(
        gate = Gate.make(
          storage = StoragePostgresImpl.make(postgres)
        )
      )
    )
}
