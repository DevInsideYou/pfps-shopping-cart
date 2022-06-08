package dev.insideyou
package shoppy
package branding

import cats.effect._
import cats.syntax.all._
import skunk.Session

object DI {
  def make[F[_]: MonadCancelThrow](postgres: Resource[F, Session[F]]): F[Controller[F]] =
    ControllerImpl
      .make(
        boundary = BoundaryImpl.make(
          gate = Gate.make(
            storage = StoragePostgresImpl.make(postgres)
          )
        )
      )
      .pure
}