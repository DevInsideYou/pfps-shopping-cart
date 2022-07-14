package dev.insideyou
package shoppy
package users
package auth

final case class Config(
    tokenExpiration: TokenExpiration,
    jwtAccessTokenKeyConfig: JwtAccessTokenKeyConfig,
    passwordSalt: PasswordSalt
)
