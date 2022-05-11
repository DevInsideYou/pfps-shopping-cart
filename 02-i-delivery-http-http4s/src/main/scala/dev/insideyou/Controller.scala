package dev.insideyou

import org.http4s.HttpRoutes
import org.http4s.server.AuthMiddleware

// TODO see if we need to seal it or replace it with a type
// trait Controller[F[_]] extends OpenController[F] with AdminController[F]

object Controller {
  trait Open[F[_]] {
    def openRoutes: HttpRoutes[F]
  }

  trait Admin[F[_]] {
    def adminRoutes: HttpRoutes[F]
  }

  trait Middleware[F[_], A] {
    def middleware: F[AuthMiddleware[F, A]]
  }
}
