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
      .flatMap[Nothing](runForeverUnderSupervision)
  }

  @scala.annotation.nowarn("cat=unused")
  private def runForeverUnderSupervision[F[_]: NonEmptyParallel: Async: Logger](
      resources: Resource[F, Resources[F]]
  ): F[Nothing] =
    Supervisor[F].use[Nothing] { implicit supervisor =>
      resources
        .evalMap(makeHttpServer[F])
        .flatMap(_.serve)
        .useForever
    }

  private def makeHttpServer[F[_]: NonEmptyParallel: Async: Logger](
      resources: Resources[F]
  ): F[HttpServer[F]] = {
    import resources._

    (
      HttpServerConfigLoader.load,
      users.auth.JwtExpire.make,
      users.auth.middleware.admin.DI.make.middleware,
      users.auth.middleware.DI.make(redis).middleware
    ).parTupled.flatMap {
      case (httpServerConfig, jwtExpire, adminAuthMiddleware, usersAuthMiddleware) =>
        List(
          branding.DI.make(postgres),
          branding.admin.DI.make(postgres, adminAuthMiddleware),
          categories.DI.make(postgres),
          categories.admin.DI.make(postgres, adminAuthMiddleware),
          health.DI.make(postgres, redis),
          items.DI.make(postgres),
          items.admin.DI.make(postgres, adminAuthMiddleware),
          ordering.DI.make(postgres, usersAuthMiddleware),
          shopping_cart.DI.make[F](postgres, redis, usersAuthMiddleware),
          users.auth.DI.make(postgres, redis, usersAuthMiddleware, jwtExpire)
        ).sequence.map(HttpApp.make[F]).map(HttpServer.make(httpServerConfig))
    }
  }
}
