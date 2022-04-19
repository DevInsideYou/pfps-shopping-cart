package dev.insideyou
package shoppy
package users

final case class UserWithPassword(
    id: UserId,
    name: UserName,
    password: EncryptedPassword
)
