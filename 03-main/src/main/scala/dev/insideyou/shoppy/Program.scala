package dev.insideyou
package shoppy

import cats._
import cats.syntax.all._
import cats.effect._
import org.typelevel.log4cats._
import dev.profunktor.redis4cats.effect._
import cats.effect.std.Supervisor

object Program {
  def make[F[+_]: NonEmptyParallel: Async: MkRedis: std.Console: Logger]: F[Unit] =
    AppEnvironmentConfigLoader.load[F].flatMap { appEnvironment =>
      (RedisSessionLoader.load[F](appEnvironment), PostgresSessionLoader.load[F]).parTupled
        .flatMap { appResources =>
          Supervisor[F].use { implicit supervisor =>
            appResources.parTupled
              .evalMap {
                case (redis, postgres) =>
                  HttpServerConfigLoader.load
                    .flatMap { httpServerConfig =>
                      for {
                        implicit0(d: users.auth.JwtExpire[F]) <- users.auth.JwtExpire.make
                        _                                     <- users.auth.middleware.admin.DI.make.middleware
                        usersAuthMiddleware                   <- users.auth.middleware.DI.make(redis).middleware
                        usersAuth                             <- users.auth.DI.make(postgres, redis, usersAuthMiddleware)
                        httpApp = HttpApp.make(
                          List(
                            usersAuth.openRoutes
                          ),
                          List.empty
                        )
                      } yield (httpServerConfig, httpApp)
                    }
              }
              .flatMap {
                case (httpServerConfig, httpApp) =>
                  HttpServer.make(httpServerConfig, httpApp).serve
              }
              .useForever
          }
        }
    }
}
