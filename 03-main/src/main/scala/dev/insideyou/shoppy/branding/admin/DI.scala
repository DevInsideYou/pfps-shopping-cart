package dev.insideyou
package shoppy
package branding
package admin

import cats.effect._
import dev.insideyou.shoppy.users.auth.middleware.admin.AdminUser // TODO move it to shoppy or even devinsideyou
import org.http4s.circe.JsonDecoder
import org.http4s.server.AuthMiddleware
import skunk.Session

object DI {
  def make[F[_]: MonadCancelThrow: GenUUID: JsonDecoder](
      postgres: Resource[F, Session[F]],
      authMiddleware: AuthMiddleware[F, AdminUser]
  ): Controller.Admin[F] =
    ControllerImpl.make(
      boundary = BoundaryImpl.make(
        gate = Gate.make(
          storage = StoragePostgresImpl.make(postgres)
        )
      ),
      authMiddleware = authMiddleware
    )
}
