package dev.insideyou
package shoppy
package users

trait Gate[F[_]] extends Storage[F]

object Gate {
  def make[F[_]](storage: Storage[F]): Gate[F] =
    new Gate[F] {
      override def find(username: UserName): F[Option[UserWithPassword]] =
        storage.find(username)

      override def create(
          username: UserName,
          password: EncryptedPassword
      ): F[Either[UserNameInUse, UserId]] =
        storage.create(username, password)
    }
}

trait Storage[F[_]] {
  def find(username: UserName): F[Option[UserWithPassword]]
  def create(username: UserName, password: EncryptedPassword): F[Either[UserNameInUse, UserId]]
}
