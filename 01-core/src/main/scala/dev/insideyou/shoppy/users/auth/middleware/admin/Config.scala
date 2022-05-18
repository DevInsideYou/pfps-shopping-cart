package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

final case class Config(tokenKey: JwtSecretKeyConfig, adminKey: AdminUserTokenConfig)
