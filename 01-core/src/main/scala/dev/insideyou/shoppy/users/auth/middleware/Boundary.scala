package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.syntax.all._

final case class AuthMiddleware[F[_], A](
    tokenKeyConfig: JwtAccessTokenKeyConfig,
    find: JwtToken => F[Option[A]]
)

trait Boundary[F[_], A] {
  def authMiddleware: F[AuthMiddleware[F, A]]
}

object BoundaryImpl {
  def make[F[_]: Monad](
      gate: Gate[F]
  ): Boundary[F, CommonUser] =
    new Boundary[F, CommonUser] {
      override lazy val authMiddleware: F[AuthMiddleware[F, CommonUser]] =
        gate.tokenKeyConfig.map { tokenKeyConfig =>
          AuthMiddleware(
            tokenKeyConfig,
            find = token =>
              for {
                userString <- gate.getUserStringFromCache(token)
                commonUser <- userString.flatTraverse(gate.convertToCommonUser)
              } yield commonUser
          )
        }
    }
}
