package dev.insideyou

import org.http4s.server.AuthMiddleware

trait Middleware[F[_], A] {
  def middleware: F[AuthMiddleware[F, A]]
}
