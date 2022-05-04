package dev.insideyou

import org.http4s.HttpRoutes

trait Controller[F[_]] extends OpenController[F] with AdminController[F]

trait OpenController[F[_]] {
  def openRoutes: HttpRoutes[F]
}

trait AdminController[F[_]] {
  def adminRoutes: HttpRoutes[F]
}
