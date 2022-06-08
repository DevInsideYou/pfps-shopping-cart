package dev.insideyou
package shoppy

import cats._
import cats.effect._
import cats.effect.std.Supervisor
import cats.syntax.all._
import dev.profunktor.redis4cats.RedisCommands
import dev.profunktor.redis4cats.effect._
import org.http4s.server.AuthMiddleware
import org.typelevel.log4cats._
import skunk.Session

object Program {
  @scala.annotation.nowarn("cat=unused")
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
                        adminAuthMiddleware                   <- users.auth.middleware.admin.DI.make.middleware
                        usersAuthMiddleware                   <- users.auth.middleware.DI.make(redis).middleware
                        httpApp <- makeHttpApp(
                          redis,
                          postgres,
                          adminAuthMiddleware,
                          usersAuthMiddleware
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

  private def makeHttpApp[F[+_]: NonEmptyParallel: Async: users.auth.JwtExpire](
      redis: RedisCommands[F, String, String],
      postgres: Resource[F, Session[F]],
      adminAuthMiddleware: AuthMiddleware[F, AdminUser],
      usersAuthMiddleware: AuthMiddleware[F, CommonUser]
  ): F[org.http4s.HttpApp[F]] =
    List(
      branding.DI.make(postgres),
      branding.admin.DI.make(postgres, adminAuthMiddleware),
      categories.DI.make(postgres),
      categories.admin.DI.make(postgres, adminAuthMiddleware),
      items.DI.make(postgres),
      items.admin.DI.make(postgres, adminAuthMiddleware),
      ordering.DI.make(postgres, usersAuthMiddleware),
      users.auth.DI.make(postgres, redis, usersAuthMiddleware)
    ).sequence.map(HttpApp.make[F])
}
