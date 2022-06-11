package dev.insideyou
package shoppy
package shopping_cart

import cats._
import cats.syntax.all._
import io.circe._
import io.circe.generic.semiauto._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

import items.CirceCodecs._

object ControllerImpl {
  def make[F[_]: JsonDecoder: Monad](
      boundary: Boundary[F],
      authMiddleware: AuthMiddleware[F, CommonUser]
  ): Controller[F] =
    new Controller.Open[F] with Http4sDsl[F] {
      override lazy val routes: HttpRoutes[F] =
        authMiddleware {
          AuthedRoutes.of[CommonUser, F] {
            case GET -> Root as user =>
              Ok(boundary.get(user.value.id))

            case ar @ POST -> Root as user =>
              ar.req.asJsonDecode[Cart].flatMap {
                _.items
                  .map {
                    case (id, quantity) =>
                      boundary.add(user.value.id, id, quantity)
                  }
                  .toList
                  .sequence *> Created()
              }

            case ar @ PUT -> Root as user =>
              ar.req.asJsonDecode[Cart].flatMap { cart =>
                boundary.update(user.value.id, cart) *> Ok()
              }

            case DELETE -> Root / vars.ItemIdVar(itemId) as user =>
              boundary.removeItem(user.value.id, itemId) *> NoContent()
          }
        }
    }

  @scala.annotation.nowarn("cat=unused")
  private implicit lazy val encoderForCartItem: Encoder[CartItem] =
    deriveEncoder

  private implicit lazy val encoderForCartTotal: Encoder[CartTotal] =
    deriveEncoder

  private implicit val jsonDecoder: Decoder[Cart] =
    Decoder.forProduct1("items")(Cart.apply)
}
