package dev.insideyou
package shoppy
package categories
package admin

import cats._
import cats.syntax.all._
import dev.insideyou.refined._
import io.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

import CirceCodecs._

object ControllerImpl {
  def make[F[_]: JsonDecoder: MonadThrow](
      dependencies: Dependencies[F]
  ): Controller[F] =
    new Controller.Admin[F] with Http4sDsl[F] {
      override lazy val routes: HttpRoutes[F] =
        Router {
          "/categories" ->
            dependencies.authMiddleware {
              AuthedRoutes.of[AdminUser, F] {
                case ar @ POST -> Root as _ =>
                  ar.req.decodeR[CategoryParam] { bp =>
                    dependencies.create(bp.toDomain).flatMap { id =>
                      Created(JsonObject.singleton("category_id", id.asJson))
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

        override def create(name: CategoryName): F[CategoryId] =
          boundary.create(name)
      }
    }
}