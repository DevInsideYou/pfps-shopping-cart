package dev.insideyou
package shoppy

import scala.concurrent.duration._

import cats.effect._
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware._

object HttpApp {
  def make[F[_]: Async](
      openRoutes: HttpRoutes[F],
      adminRoutes: HttpRoutes[F]
  ): HttpApp[F] = {
    lazy val routes: HttpRoutes[F] =
      Router(
        version.v1            -> openRoutes,
        version.v1 + "/admin" -> adminRoutes
      )

    lazy val middleware: HttpRoutes[F] => HttpRoutes[F] =
      List[HttpRoutes[F] => HttpRoutes[F]](
        AutoSlash(_),
        CORS(_),
        Timeout(60.seconds)(_)
      ).reduceLeft(_ andThen _)

    lazy val loggers: HttpApp[F] => HttpApp[F] =
      List[HttpApp[F] => HttpApp[F]](
        RequestLogger.httpApp(true, true),
        ResponseLogger.httpApp(true, true)
      ).reduceLeft(_ andThen _)

    loggers(middleware(routes).orNotFound)
  }
}
