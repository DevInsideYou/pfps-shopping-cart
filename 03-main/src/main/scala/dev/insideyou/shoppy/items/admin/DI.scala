package dev.insideyou
package shoppy
package items
package admin

import cats.syntax.all._
import cats.effect._
import org.http4s.circe.JsonDecoder
import org.http4s.server.AuthMiddleware
import skunk.Session

object DI {
  def make[F[_]: MonadCancelThrow: GenUUID: JsonDecoder](
      postgres: Resource[F, Session[F]],
      authMiddleware: AuthMiddleware[F, AdminUser]
  ): F[Controller[F]] =
    ControllerImpl
      .make(
        boundary = BoundaryImpl.make(
          gate = Gate.make(
            persistence = PersistenceImpl.make(postgres)
          )
        ),
        authMiddleware = authMiddleware
      )
      .pure
}
