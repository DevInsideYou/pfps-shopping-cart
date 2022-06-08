package dev.insideyou
package shoppy
package users
package auth

import cats._
import cats.data.NonEmptyList
import cats.syntax.all._
import derevo.circe.magnolia._
import derevo.derive
import dev.insideyou.refined._
import dev.profunktor.auth._
import eu.timepit.refined.auto._
import eu.timepit.refined.types.all._
import io.circe.Encoder
import io.circe.refined._
import io.estatico.newtype.macros.newtype
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe.JsonDecoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server._

object ControllerImpl {
  def make[F[_]: JsonDecoder: MonadThrow](
      boundary: Boundary[F, jwt.JwtToken],
      authMiddleware: AuthMiddleware[F, CommonUser]
  ): Controller[F] =
    new Controller.Open[F] with Http4sDsl[F] {
      protected implicit lazy val s: Semigroup[HttpRoutes[F]] =
        _ combineK _

      override lazy val routes: HttpRoutes[F] =
        Router("/auth" -> NonEmptyList.of(notAuthedRoutes, authedRoutes).reduce)

      private lazy val notAuthedRoutes: HttpRoutes[F] =
        HttpRoutes.of[F] {
          case req @ POST -> Root / "users" =>
            req.decodeR[CreateUser](users)

          case req @ POST -> Root / "login" =>
            req.decodeR[LoginUser](login)
        }

      private lazy val authedRoutes: HttpRoutes[F] =
        authMiddleware {
          AuthedRoutes.of[CommonUser, F] {
            case ar @ POST -> Root / "logout" as user =>
              AuthHeaders
                .getBearerToken(ar.req)
                .traverse_(t => boundary.logout(t, user.value.name)) *> NoContent()
          }
        }

      private def users(createUser: CreateUser): F[Response[F]] =
        boundary
          .newUser(createUser.username.toDomain, createUser.password.toDomain)
          .flatMap(Created(_))
          .recoverWith {
            case UserNameInUse(u) => Conflict(u.show)
          }

      private def login(loginUser: LoginUser): F[Response[F]] =
        boundary
          .login(loginUser.username.toDomain, loginUser.password.toDomain)
          .flatMap(Ok(_))
          .recoverWith {
            case UserNotFound(_) | InvalidPassword(_) => Forbidden()
          }
    }

  private implicit lazy val tokenEncoder: Encoder[jwt.JwtToken] =
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

  @derive(decoder, encoder)
  final case class LoginUser(
      username: UserNameParam,
      password: PasswordParam
  )
}
