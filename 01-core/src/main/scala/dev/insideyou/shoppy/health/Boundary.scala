package dev.insideyou
package shoppy
package health

import scala.concurrent.duration._

import cats._
import cats.syntax.all._

import health_package_object._

trait Boundary[F[_]] {
  def status: F[AppStatus]
}

object BoundaryImpl {
  def make[F[_]: NonEmptyParallel](gate: Gate[F]): Boundary[F] =
    new Boundary[F] {
      override lazy val status: F[AppStatus] =
        (
          gate.redisStatus(timeout),
          gate.postgresStatus(timeout)
        ).parMapN(AppStatus)

      private lazy val timeout =
        1.second
    }
}
