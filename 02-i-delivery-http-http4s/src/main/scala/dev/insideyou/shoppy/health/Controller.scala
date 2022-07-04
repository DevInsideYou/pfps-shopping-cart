package dev.insideyou
package shoppy
package health

import cats.Monad
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

import CirceCodecs._

object ControllerImpl {
  def make[F[_]: Monad](boundary: Boundary[F]): Controller[F] =
    new Controller.Open[F] with Http4sDsl[F] {
      override lazy val routes: HttpRoutes[F] =
        Router {
          "/healthcheck" -> HttpRoutes.of[F] {
            case GET -> Root =>
              Ok(boundary.status)
          }
        }
    }

}
