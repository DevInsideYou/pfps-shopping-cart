package dev.insideyou
package shoppy
package users
package auth

import cats.syntax.all._
import cats.effect._

import skunk._
import skunk.codec.all._
import skunk.implicits._

object StoragePostgresImpl {
  def make[F[_]: GenUUID: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Storage[F] =
    new Storage[F] {
      override def findUser(username: UserName): F[Option[UserWithPassword]] =
        postgres.use { session =>
          session.prepare(SQL.selectUser).use { q =>
            q.option(username).map {
              case Some(u ~ p) => UserWithPassword(u.id, u.name, p).some
              case _           => none[UserWithPassword]
            }
          }
        }

      override def createUser(
          username: UserName,
          password: EncryptedPassword
      ): F[Either[UserNameInUse, UserId]] =
        postgres.use { session =>
          session.prepare(SQL.insertUser).use { cmd =>
            ID.make[F, UserId].flatMap { id =>
              cmd
                .execute(SQL.User(id, username) ~ password)
                .as(id.asRight[UserNameInUse])
                .recoverWith {
                  case SqlState.UniqueViolation(_) =>
                    UserNameInUse(username).asLeft.pure
                }
            }
          }
        }
    }

  object SQL {
    lazy val userId: Codec[UserId] =
      uuid.imap[UserId](UserId(_))(_.value)

    lazy val userName: Codec[UserName] =
      varchar.imap[UserName](UserName(_))(_.value)

    lazy val encPassword: Codec[EncryptedPassword] =
      varchar.imap[EncryptedPassword](EncryptedPassword(_))(_.value)

    final case class User(id: UserId, name: UserName)

    lazy val codec: Codec[User ~ EncryptedPassword] =
      (userId ~ userName ~ encPassword).imap {
        case i ~ n ~ p =>
          User(i, n) ~ p
      } {
        case u ~ p =>
          u.id ~ u.name ~ p
      }

    lazy val selectUser: Query[UserName, User ~ EncryptedPassword] =
      sql"""
        SELECT * FROM users
        WHERE name = $userName
       """.query(codec)

    lazy val insertUser: Command[User ~ EncryptedPassword] =
      sql"""
        INSERT INTO users
        VALUES ($codec)
        """.command
  }
}