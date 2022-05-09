package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats._
import pdi.jwt.JwtClaim

object DI {
  def make[F[_]: Applicative](
      adminToken: JwtToken,
      adminUser: AdminUser
  ): Boundary[F, AdminUser, JwtClaim] =
    BoundaryImpl.make(adminToken, adminUser)
}
