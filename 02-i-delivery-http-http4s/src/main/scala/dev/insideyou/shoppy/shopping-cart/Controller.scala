package dev.insideyou
package shoppy
package shopping_cart

import cats._
import cats.syntax.all._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

object ControllerImpl {
  def make[F[_]: JsonDecoder: Monad](
      boundary: Boundary[F],
      authMiddleware: AuthMiddleware[F, CommonUser]
  ): Controller[F] =
    new Controller.Open[F] with Http4sDsl[F] {
      import CirceCodecs._

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
}
