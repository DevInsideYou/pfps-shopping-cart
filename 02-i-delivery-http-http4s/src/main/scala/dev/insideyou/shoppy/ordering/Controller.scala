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
      boundary: Boundary[F],
      authMiddleware: AuthMiddleware[F, CommonUser]
  ): Controller[F] =
    new Controller.Open[F] with Http4sDsl[F] {
      override lazy val routes: HttpRoutes[F] =
        authMiddleware {
          AuthedRoutes.of[CommonUser, F] {
            case GET -> Root as user =>
              Ok(boundary.findBy(user.value.id))

            case GET -> Root / vars.OrderIdVar(orderId) as user =>
              Ok(boundary.get(user.value.id, orderId))
          }
        }
    }
}
