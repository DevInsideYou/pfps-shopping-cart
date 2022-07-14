package dev.insideyou

import scala.concurrent.duration.FiniteDuration

import cats.effect._
import cats.effect.std.Supervisor
import cats.syntax.all._

object BackgroundImpl {
  def make[F[_]](supervisor: Supervisor[F])(implicit temporal: Temporal[F]): Background[F] =
    new Background[F] {
      def schedule[A](fa: F[A], duration: FiniteDuration): F[Unit] =
        supervisor
          .supervise(temporal.sleep(duration).productR(fa))
          .void
    }
}
