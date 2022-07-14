package dev.insideyou
package shoppy
package users
package auth

import cats.effect._
import cats.syntax.all._
import skunk._
import skunk.implicits._

object PersistenceImpl {
  def make[F[_]: GenUUID: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  ): Persistence[F] =
    new Persistence[F] {
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
}
