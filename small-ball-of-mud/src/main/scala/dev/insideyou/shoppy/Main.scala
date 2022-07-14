package dev.insideyou
package shoppy

import cats.effect._

object Main extends IOApp.Simple {
  override lazy val run: IO[Unit] =
    Program.make[IO]
}
