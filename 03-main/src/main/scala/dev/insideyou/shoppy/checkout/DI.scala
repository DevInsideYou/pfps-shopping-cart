package dev.insideyou
package shoppy
package checkout

import cats.effect._
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands
import org.http4s.circe.JsonDecoder
import org.http4s.client.Client
import org.http4s.server.AuthMiddleware
import org.typelevel.log4cats
import retry.RetryPolicy
import skunk.Session

object DI {
  def make[F[_]: MonadCancelThrow: log4cats.Logger: JsonDecoder: Retry: GenUUID: Background](
      postgres: Resource[F, Session[F]],
      redis: RedisCommands[F, String, String],
      config: PaymentConfig, // I'm still not sure about this one
      client: Client[F],
      shoppingCartBoundary: shopping_cart.Boundary[F],
      authMiddleware: AuthMiddleware[F, CommonUser],
      policy: RetryPolicy[F]
  ): F[Controller[F]] =
    ControllerImpl
      .make(
        boundary = BoundaryImpl.make(
          gate = Gate.make(
            hasLogger = HasLoggerImpl.make(implicitly[log4cats.Logger[F]], "checkout"),
            paymentClient = PaymentClientImpl.make(config, client),
            storage = StoragePostgresImpl.make(postgres, policy),
            redis = RedisImpl.make(redis),
            otherBoundaries = OtherBoundariesImpl.make(shoppingCartBoundary)
          )
        ),
        authMiddleware
      )
      .pure
}
