package dev.insideyou
package shoppy

import cats._
import cats.effect._
import cats.syntax.all._
import dev.insideyou.shoppy.users.auth.JwtExpire
import org.http4s.server.AuthMiddleware
import org.typelevel.log4cats._
import retry.RetryPolicy

object Controllers {
  def make[F[_]: NonEmptyParallel: Async: Logger: Background](
      resources: Resources[F]
  )(
      policy: RetryPolicy[F],
      jwtExpire: JwtExpire[F],
      admin: AuthMiddleware[F, AdminUser],
      user: AuthMiddleware[F, CommonUser]
  ): F[List[Controller[F]]] = {
    import resources._

    for {
      (shoppingCartController, cartBoundary) <- shopping_cart.DI.make(postgres, redis, user)

      controllers <- List(
        branding.DI.make(postgres),
        branding.admin.DI.make(postgres, admin),
        categories.DI.make(postgres),
        categories.admin.DI.make(postgres, admin),
        health.DI.make(postgres, redis),
        items.DI.make(postgres),
        items.admin.DI.make(postgres, admin),
        ordering.DI.make(postgres, user),
        shoppingCartController.pure,
        users.auth.DI.make(postgres, redis, user, jwtExpire),
        checkout.DI.make(postgres, redis, clientResources, cartBoundary, user, policy)
      ).sequence
    } yield controllers
  }
}
