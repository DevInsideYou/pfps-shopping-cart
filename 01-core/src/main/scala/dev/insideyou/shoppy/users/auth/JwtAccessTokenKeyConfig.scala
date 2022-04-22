package dev.insideyou
package shoppy
package users
package auth

import eu.timepit.refined.types.all._

final case class JwtAccessTokenKeyConfig(secret: NonEmptyString)
