package dev.insideyou
package shoppy
package items

import scala.util.chaining._

import cats.Monad
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

import CirceCodecs._

object ControllerImpl {
  def make[F[_]: Monad](boundary: Boundary[F]): Controller.Open[F] =
    new Controller.Open[F] with Http4sDsl[F] {
      object BrandQueryParam
          extends OptionalQueryParamDecoderMatcher[branding.package_object_2.BrandParam]("brand")

      override lazy val routes: HttpRoutes[F] =
        Router {
          "/items" -> HttpRoutes.of[F] {
            case GET -> Root :? BrandQueryParam(brand) =>
              Ok(brand.fold(boundary.findAll)(_.toDomain.pipe(boundary.findBy)))
          }
        }
    }
}
