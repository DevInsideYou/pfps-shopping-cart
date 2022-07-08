package dev.insideyou

import org.http4s.server._

trait HasAuthMiddleware[F[_], T] {
  def authMiddleware: AuthMiddleware[F, T]
}
