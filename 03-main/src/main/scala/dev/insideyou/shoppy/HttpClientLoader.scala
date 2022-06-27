package dev.insideyou
package shoppy

import scala.concurrent.duration._
import scala.util.chaining._

import cats._
import cats.effect._
import cats.syntax.all._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

object HttpClientLoader {
  def load[F[_]: Async: NonEmptyParallel](
      appEnvironment: AppEnvironment
  ): F[Resource[F, HttpClientResources[F]]] =
    (
      buildClient,
      paymentConfig[F](appEnvironment).pure
    ).parTupled.map(_.mapN(HttpClientResources.apply))

  private def buildClient[F[_]: Async]: F[Resource[F, Client[F]]] =
    config.map { c =>
      EmberClientBuilder.default
        .withTimeout(c.timeout)
        .withIdleTimeInPool(c.idleTimeInPool)
        .build
    }

  private def config[F[_]: Async]: F[Config] =
    Config(
      timeout = 60.seconds,
      idleTimeInPool = 30.seconds
    ).pure

  private final case class Config(
      timeout: FiniteDuration,
      idleTimeInPool: FiniteDuration
  )

  private def paymentConfig[F[_]](
      appEnvironment: AppEnvironment
  ): Resource[F, PaymentConfig] =
    appEnvironment
      .pipe[NonEmptyString] {
        case AppEnvironment.Test => "https://payments.free.beeceptor.com"
        case AppEnvironment.Prod => "https://payments.net/api"
      }
      .pipe(PaymentURI.apply)
      .pipe(PaymentConfig.apply)
      .pipe(Resource.pure)
}

final case class HttpClientResources[F[_]](
    client: Client[F],
    config: PaymentConfig
)
