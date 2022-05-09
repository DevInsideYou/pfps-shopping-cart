package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats.Applicative
import cats.syntax.all._

object BoundaryImpl {
  def make[F[_]: Applicative, JwtClaim](
      adminToken: JwtToken,
      adminUser: AdminUser
  ): Boundary[F, AdminUser, JwtClaim] =
    new Boundary[F, AdminUser, JwtClaim] {
      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[AdminUser]] =
        (token === adminToken)
          .guard[Option]
          .as(adminUser)
          .pure
    }
}
