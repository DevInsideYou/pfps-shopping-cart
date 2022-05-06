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
          commonUser <- userString.traverse(gate.convertToCommonUser)
        } yield commonUser
    }

  // TODO: move me (maybe???)
  def admin[F[_]: Applicative, JwtClaim, AdminUser](
      adminToken: JwtToken,
      adminUser: AdminUser
  ): Boundary[F, AdminUser, JwtClaim] =
    new Boundary[F, AdminUser, JwtClaim] {
      def findUser(token: JwtToken)(claim: JwtClaim): F[Option[AdminUser]] =
        (token === adminToken)
          .guard[Option]
          .as(adminUser)
          .pure[F]
    }
}

trait Gate[F[_]] {
  def getUserStringFromCache(token: JwtToken): F[Option[String]]
  def convertToCommonUser(userString: String): F[CommonUser]
}
