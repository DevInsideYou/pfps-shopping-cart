package dev.insideyou
package shoppy

import cats._
import cats.effect._
import cats.effect.std.Supervisor
import cats.syntax.all._
import dev.profunktor.redis4cats.log4cats._
import org.typelevel.log4cats._
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Program {
  def make[F[_]: NonEmptyParallel: Async: std.Console]: F[Nothing] = {
    implicit val logger: Logger[F] =
      Slf4jLogger.getLogger

    AppEnvironmentConfigLoader.load
      .flatMap(ResourcesLoader.load[F])
      .flatMap[Nothing](runHttpServerForeverUnderSupervision)
  }

  private def runHttpServerForeverUnderSupervision[F[_]: NonEmptyParallel: Async: Logger](
      resources: Resource[F, Resources[F]]
  ): F[Nothing] =
    Supervisor[F].use[Nothing] { supervisor =>
      implicit lazy val background: Background[F] =
        BackgroundImpl.make(supervisor)

      resources
        .evalMap(makeHttpServer[F])
        .flatMap(_.serve)
        .useForever
    }

  private def makeHttpServer[F[_]: NonEmptyParallel: Async: Logger: Background](
      resources: Resources[F]
  ): F[HttpServer[F]] =
    for {
      httpServerConfig <- HttpServerConfigLoader.load
      otherDependencies <- (
        RetryPolicyLoader.load,
        JwtExpire.make,
        users.auth.middleware.admin.DI.make.middleware,
        users.auth.middleware.DI.make(resources.redis).middleware
      ).parTupled
      controllers <- Function.tupled(Controllers.make(resources) _)(otherDependencies)
    } yield HttpServer.make(httpServerConfig, HttpApp.make(controllers))
}
