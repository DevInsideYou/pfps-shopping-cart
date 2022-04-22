package dev.insideyou
package shoppy
package users
package auth

import org.http4s.circe.CirceEntityEncoder._
import cats.MonadThrow
import cats.syntax.all._
import derevo.circe.magnolia._
import derevo.derive
import dev.insideyou.refined._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.all._
import io.circe.refined._
import io.estatico.newtype.macros.newtype
import org.http4s._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import io.circe.Encoder

object Controller {
  def make[F[_]: JsonDecoder: MonadThrow](boundary: Boundary[F]): Controller[F] =
    new Controller[F] with Http4sDsl[F] {
      override lazy val routes: HttpRoutes[F] =
        Router {
          "/auth" -> HttpRoutes.of[F] {
            case req @ POST -> Root / "users" =>
              req.decodeR[CreateUser](users)
          }
        }

      private def users(createUser: CreateUser): F[Response[F]] =
        boundary
          .newUser(createUser.username.toDomain, createUser.password.toDomain)
          .flatMap(Created(_))
          .recoverWith {
            case UserNameInUse(u) => Conflict(u.show)
          }
    }

  implicit val tokenEncoder: Encoder[JwtToken] =
    Encoder.forProduct1("access_token")(_.value)

  @derive(decoder, encoder)
  final case class CreateUser(
      username: UserNameParam,
      password: PasswordParam
  )

  @derive(decoder, encoder)
  @newtype
  final case class UserNameParam(value: NonEmptyString) {
    def toDomain: UserName = UserName(value.toLowerCase)
  }

  @derive(decoder, encoder)
  @newtype
  final case class PasswordParam(value: NonEmptyString) {
    def toDomain: Password = Password(value)
  }
}
