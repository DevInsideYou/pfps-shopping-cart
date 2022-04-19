package dev.insideyou
package shoppy
package users

import cats._
import cats.syntax.all._

trait Users[F[_]] {
  def find(username: UserName): F[Option[UserWithPassword]]
  def create(username: UserName, password: EncryptedPassword): F[UserId]
}

object UsersImpl {
  def make[F[_]: MonadThrow](gate: Gate[F]): Users[F] =
    new Users[F] {
      override def find(username: UserName): F[Option[UserWithPassword]] =
        gate.find(username)

      override def create(username: UserName, password: EncryptedPassword): F[UserId] =
        gate
          .create(username, password)
          .flatMap(_.fold(_.raiseError, _.pure))
    }
}
