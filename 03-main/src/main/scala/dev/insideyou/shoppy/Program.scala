package dev.insideyou
package shoppy

import cats._
import cats.syntax.all._
import cats.effect._
import org.typelevel.log4cats._
import org.typelevel.log4cats.slf4j._
import dev.profunktor.redis4cats.effect._

object Program {
  def make[F[_]: NonEmptyParallel: Async: MkRedis: std.Console: Logger]: F[Unit] =
    AppEnvironmentConfigLoader.load[F].flatMap { appEnvironment =>
      RedisConfigLoader.load[F](appEnvironment).flatMap { redisConfig =>
        PostgresSqlConfigLoader.load[F].flatMap { postgresConfig =>
          implicit val b: CheckRedisConnection[F]    = CheckRedisConnection.make
          implicit val c: CheckPostgresConnection[F] = CheckPostgresConnection.make

          RedisSession.make(redisConfig).use { redis =>
            StoragePostgresSession.make(postgresConfig).use { postgres =>
              HttpServerConfigLoader.load.flatMap { httpServerConfig =>
                for {
                  implicit0(d: users.auth.JwtExpire[F]) <- users.auth.JwtExpire.make
                  usersAuthMiddlewareAdmin              <- users.auth.middleware.admin.DI.make.middleware
                  usersAuthMiddleware                   <- users.auth.middleware.DI.make(redis).middleware
                  usersAuth <- users.auth.DI
                    .make(postgres, redis, usersAuthMiddleware)
                    .map(_.openRoutes)
                  httpApp = HttpApp.make(usersAuth, usersAuth) // TODO fix this
                  _ <- HttpServer
                    .make(httpServerConfig, httpApp)
                    .serve
                    .use(_ => Applicative[F].unit)
                } yield ()
              }
            }
          }
        }
      }
    }
}
