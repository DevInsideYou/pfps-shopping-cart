package dev.insideyou
package shoppy
package checkout

import cats.effect._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands
import org.http4s.circe.JsonDecoder
import org.http4s.server.AuthMiddleware
import org.typelevel.log4cats
import retry.RetryPolicy
import skunk.Session

object DI {
  def make[F[_]: log4cats.Logger: JsonDecoder: GenUUID: Background: Temporal](
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String],
      httpClientResources: HttpClientResources[F],
      shoppingCartBoundary: shopping_cart.Boundary[F],
      authMiddleware: AuthMiddleware[F, CommonUser],
      retryPolicy: RetryPolicy[F]
  ): F[Controller[F]] = {
    lazy val hasLogger       = HasLoggerImpl.make(implicitly[log4cats.Logger[F]], "checkout")
    implicit lazy val logger = hasLogger.logger

    ControllerImpl
      .make(
        boundary = BoundaryImpl.make(
          gate = Gate.make(
            hasLogger = hasLogger,
            paymentClient =
              PaymentClientImpl.make(httpClientResources.config, httpClientResources.client),
            storage = StoragePostgresImpl.make(postgres, retryPolicy),
            redis = RedisImpl.make(redis),
            otherBoundaries = OtherBoundariesImpl.make(shoppingCartBoundary)
          )
        ),
        authMiddleware
      )
      .pure
  }
}
