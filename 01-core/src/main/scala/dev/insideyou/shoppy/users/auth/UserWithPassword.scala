package dev.insideyou
package shoppy
package users
package auth

final case class UserWithPassword(
    id: UserId,
    name: UserName,
    password: EncryptedPassword
)
