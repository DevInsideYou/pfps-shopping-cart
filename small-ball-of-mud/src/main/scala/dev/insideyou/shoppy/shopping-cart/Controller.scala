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
      dependencies: Dependencies[F]
  ): Controller[F] =
    new Controller.Open[F] with Http4sDsl[F] {
      import CirceCodecs._

      override lazy val routes: HttpRoutes[F] =
        dependencies.authMiddleware {
          AuthedRoutes.of[CommonUser, F] {
            case GET -> Root as user =>
              Ok(dependencies.get(user.value.id))

            case ar @ POST -> Root as user =>
              ar.req.asJsonDecode[Cart].flatMap {
                _.items
                  .map {
                    case (id, quantity) =>
                      dependencies.add(user.value.id, id, quantity)
                  }
                  .toList
                  .sequence *> Created()
              }

            case ar @ PUT -> Root as user =>
              ar.req.asJsonDecode[Cart].flatMap { cart =>
                dependencies.update(user.value.id, cart) *> Ok()
              }

            case DELETE -> Root / vars.ItemIdVar(itemId) as user =>
              dependencies.removeItem(user.value.id, itemId) *> NoContent()
          }
        }
    }

  trait Dependencies[F[_]] extends HasAuthMiddleware[F, CommonUser] with Boundary[F]

  def make[F[_]: JsonDecoder: Monad](
      _authMiddleware: AuthMiddleware[F, CommonUser],
      boundary: Boundary[F]
  ): Controller[F] =
    make(
      new Dependencies[F] {
        override def authMiddleware: AuthMiddleware[F, CommonUser] =
          _authMiddleware

        override def add(userId: UserId, itemId: items.ItemId, quantity: items.Quantity): F[Unit] =
          boundary.add(userId, itemId, quantity)

        override def get(userId: UserId): F[CartTotal] =
          boundary.get(userId)

        override def removeItem(userId: UserId, itemId: items.ItemId): F[Unit] =
          boundary.removeItem(userId, itemId)

        override def update(userId: UserId, cart: Cart): F[Unit] =
          boundary.update(userId, cart)
      }
    )
}
