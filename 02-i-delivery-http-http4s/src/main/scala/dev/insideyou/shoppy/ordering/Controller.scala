package dev.insideyou
package shoppy
package ordering

import cats._
import eu.timepit.refined.auto._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

import CirceCodecs._

object ControllerImpl {
  def make[F[_]: MonadThrow](
      dependencies: Dependencies[F]
  ): Controller[F] =
    new Controller.Open[F] with Http4sDsl[F] {
      override lazy val routes: HttpRoutes[F] =
        dependencies.authMiddleware {
          AuthedRoutes.of[CommonUser, F] {
            case GET -> Root as user =>
              Ok(dependencies.findBy(user.value.id))

            case GET -> Root / vars.OrderIdVar(orderId) as user =>
              Ok(dependencies.get(user.value.id, orderId))
          }
        }
    }

  trait Dependencies[F[_]] extends HasAuthMiddleware[F, CommonUser] with Boundary[F]

  def make[F[_]: MonadThrow](
      _authMiddleware: AuthMiddleware[F, CommonUser],
      boundary: Boundary[F]
  ): Controller[F] =
    make {
      new Dependencies[F] {
        override def authMiddleware: AuthMiddleware[F, CommonUser] =
          _authMiddleware

        override def get(userId: UserId, orderId: OrderId): F[Option[ordering.Order]] =
          boundary.get(userId, orderId)

        override def findBy(userId: UserId): F[List[ordering.Order]] =
          boundary.findBy(userId)
      }
    }
}
