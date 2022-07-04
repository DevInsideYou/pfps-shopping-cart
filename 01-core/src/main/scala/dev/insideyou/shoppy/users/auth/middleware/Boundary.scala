package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.syntax.all._

final case class AuthMiddleware[F[_], A, Auth, Token](
    auth: Auth,
    find: Token => F[Option[A]]
)

trait Boundary[F[_], A, Auth, Token] {
  def authMiddleware: F[AuthMiddleware[F, A, Auth, Token]]
}

object BoundaryImpl {
  def make[F[_]: Monad, Auth, Token](
      gate: Gate[F, Auth, Token]
  ): Boundary[F, CommonUser, Auth, Token] =
    new Boundary[F, CommonUser, Auth, Token] {
      override lazy val authMiddleware: F[AuthMiddleware[F, CommonUser, Auth, Token]] =
        gate.config.map(_.tokenKey).flatMap(gate.auth).map { auth =>
          AuthMiddleware(
            auth,
            find = token =>
              gate
                .getUserFromCache(token)
                .nested
                .map(CommonUser.apply)
                .value
          )
        }
    }
}
