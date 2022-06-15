package dev.insideyou
package shoppy
package health

import cats.Monad
import io.circe.Encoder
import io.circe.magnolia.derivation.encoder.semiauto._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

import health_package_object._

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

  private implicit lazy val jsonEncoder: Encoder[health_package_object.Status] =
    Encoder.forProduct1("status")(_.toString)

  @scala.annotation.nowarn("cat=unused")
  private implicit lazy val encoderForRedisStatus: Encoder[RedisStatus] =
    RedisStatus.deriving

  @scala.annotation.nowarn("cat=unused")
  private implicit lazy val encoderForPostgresStatus: Encoder[PostgresStatus] =
    PostgresStatus.deriving

  private implicit lazy val encoderForAppStatus: Encoder[AppStatus] =
    deriveMagnoliaEncoder
}
