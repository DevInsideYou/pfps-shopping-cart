package dev.insideyou
package shoppy
package branding

import cats.Monad
import cats.syntax.all._
import derevo.circe.magnolia.encoder
import derevo.derive
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

import CirceCodecs._

object ControllerImpl {
  def make[F[_]: Monad](
      boundary: Boundary[F]
  ): Controller.Open[F] =
    new Controller.Open[F] with Http4sDsl[F] {

      override def openRoutes: HttpRoutes[F] =
        Router {
          "/brands" -> HttpRoutes.of[F] {
            case GET -> Root =>
              Ok(boundary.findAll.nested.map(Brand.fromDomain).value)
          }
        }
    }

  @derive(encoder)
  private final case class Brand(uuid: BrandId, name: BrandName)
  private object Brand {
    def fromDomain(brand: branding.Brand): Brand =
      Brand(brand.uuid, brand.name)
  }
}
