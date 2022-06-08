package dev.insideyou

import org.http4s.HttpRoutes

sealed trait Controller[F[_]] {
  def routes: HttpRoutes[F]
}

object Controller {
  trait Open[F[_]]  extends Controller[F]
  trait Admin[F[_]] extends Controller[F]
}
