package dev.insideyou
package shoppy
package users
package auth

import skunk._
import skunk.codec.all._
import skunk.implicits._

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
