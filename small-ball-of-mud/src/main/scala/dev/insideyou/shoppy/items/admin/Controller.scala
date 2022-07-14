package dev.insideyou
package shoppy
package items
package admin

import cats._
import cats.syntax.all._
import derevo.cats.show
import derevo.circe.magnolia._
import derevo.derive
import dev.insideyou.refined._
import eu.timepit.refined._
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.cats._
import eu.timepit.refined.types.all._
import io.circe._
import io.circe.refined._
import io.circe.syntax._
import io.estatico.newtype.macros.newtype
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server._
import squants.market.USD

import CirceCodecs._
import branding.CirceCodecs._
import categories.CirceCodecs._

object ControllerImpl {
  def make[F[_]: JsonDecoder: MonadThrow](
      dependencies: Dependencies[F]
  ): Controller[F] =
    new Controller.Admin[F] with Http4sDsl[F] {
      override lazy val routes: HttpRoutes[F] =
        Router {
          "/items" ->
            dependencies.authMiddleware {
              AuthedRoutes.of[AdminUser, F] {
                case ar @ POST -> Root as _ =>
                  ar.req.decodeR[CreateItemParam] { item =>
                    dependencies.create(item.toDomain).flatMap { id =>
                      Created(JsonObject.singleton("item_id", id.asJson))
                    }
                  }
              }
            }
        }
    }

  trait Dependencies[F[_]] extends HasAuthMiddleware[F, AdminUser] with Boundary[F]

  def make[F[_]: JsonDecoder: MonadThrow](
      _authMiddleware: AuthMiddleware[F, AdminUser],
      boundary: Boundary[F]
  ): Controller[F] =
    make {
      new Dependencies[F] {
        override def authMiddleware: AuthMiddleware[F, AdminUser] =
          _authMiddleware

        override def create(item: CreateItem): F[ItemId] =
          boundary.create(item)

        override def update(item: UpdateItem): F[Unit] =
          boundary.update(item)
      }
    }

  @derive(decoder, encoder, show)
  @newtype
  final case class ItemNameParam(value: NonEmptyString)

  @derive(decoder, encoder, show)
  @newtype
  final case class ItemDescriptionParam(value: NonEmptyString)

  @derive(decoder, encoder, show)
  @newtype
  final case class PriceParam(value: String Refined string.ValidBigDecimal)

  @derive(decoder, encoder, show)
  case class CreateItemParam(
      name: ItemNameParam,
      description: ItemDescriptionParam,
      price: PriceParam,
      brandId: branding.BrandId,
      categoryId: categories.CategoryId
  ) {
    def toDomain: CreateItem =
      CreateItem(
        ItemName(name.value),
        ItemDescription(description.value),
        USD(BigDecimal(price.value)),
        brandId,
        categoryId
      )
  }
}
