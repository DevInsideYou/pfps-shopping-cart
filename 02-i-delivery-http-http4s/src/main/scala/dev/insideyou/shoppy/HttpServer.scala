package dev.insideyou
package shoppy

import cats.effect._
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.defaults.Banner
import org.typelevel.log4cats.Logger

trait HttpServer[F[_]] {
  def serve: Resource[F, Server]
}

object HttpServer {
  def make[F[_]: Async: Logger](cfg: HttpServerConfig)(httpApp: HttpApp[F]): HttpServer[F] =
    new HttpServer[F] {
      def serve: Resource[F, Server] =
        EmberServerBuilder
          .default[F]
          .withHost(cfg.host)
          .withPort(cfg.port)
          .withHttpApp(httpApp)
          .build
          .evalTap(showEmberBanner)

      private def showEmberBanner(s: Server): F[Unit] =
        Logger[F].info(s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}")
    }

  def apply[F[_]: HttpServer]: HttpServer[F] = implicitly
}
