package dev.insideyou
package shoppy
package users
package auth
package middleware

import cats._
import cats.syntax.all._

trait Boundary[F[_], A, JwtClaim] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[A]]
}

object BoundaryImpl {
  def make[F[_]: Monad, JwtClaim](
      gate: Gate[F]
  ): Boundary[F, CommonUser, JwtClaim] =
    new Boundary[F, CommonUser, JwtClaim] {
      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[CommonUser]] =
        for {
          userString <- gate.getUserStringFromCache(token)
          commonUser <- userString.flatTraverse(gate.convertToCommonUser)
        } yield commonUser
    }
}
