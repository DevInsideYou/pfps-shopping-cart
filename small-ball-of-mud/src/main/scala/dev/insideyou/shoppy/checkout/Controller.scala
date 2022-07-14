package dev.insideyou
package shoppy
package checkout

import cats._
import cats.syntax.all._
import dev.insideyou.refined._
import io.circe._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

import CirceCodecs._
import ordering.CirceCodecs._

object ControllerImpl {
  def make[F[_]: JsonDecoder: MonadThrow](
      dependencies: Dependencies[F]
  ): Controller[F] =
    new Controller.Admin[F] with Http4sDsl[F] {
      override lazy val routes: HttpRoutes[F] =
        Router {
          "/checkout" ->
            dependencies.authMiddleware {
              AuthedRoutes.of[CommonUser, F] {
                case ar @ POST -> Root as user =>
                  ar.req.decodeR[Card] { card =>
                    dependencies
                      .process(user.value.id, card)
                      .flatMap(Created(_))
                      .recoverWith {
                        case CartNotFound(userId) =>
                          NotFound(s"Cart not found for user: ${userId.value}")

                        case EmptyCartError =>
                          BadRequest("Shopping cart is empty!")

                        case e: Error =>
                          BadRequest(e.show)
                      }
                  }
              }
            }
        }
    }

  trait Dependencies[F[_]] extends HasAuthMiddleware[F, CommonUser] with Boundary[F]

  def make[F[_]: JsonDecoder: MonadThrow](
      _authMiddleware: AuthMiddleware[F, CommonUser],
      boundary: Boundary[F]
  ): Controller[F] =
    make {
      new Dependencies[F] {
        override def authMiddleware: AuthMiddleware[F, CommonUser] =
          _authMiddleware

        override def process(userId: UserId, card: Card): F[ordering.OrderId] =
          boundary.process(userId, card)
      }
    }
}
