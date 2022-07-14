package dev.insideyou
package shoppy

import scala.concurrent.duration._

import cats.effect._
import cats.syntax.all._
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.middleware._

object HttpApp {
  def make[F[_]: Async](
      controllers: List[Controller[F]]
  ): HttpApp[F] = {
    val (openRoutes, adminRoutes) =
      controllers
        .foldLeft((Vector.empty[HttpRoutes[F]], Vector.empty[HttpRoutes[F]])) {
          case ((open, admin), c: Controller.Open[F]) =>
            open.appended(c.routes) -> admin

          case ((open, admin), c: Controller.Admin[F]) =>
            open -> admin.appended(c.routes)
        }
        .bimap(_.reduceLeft(_ combineK _), _.reduceLeft(_ combineK _))

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
