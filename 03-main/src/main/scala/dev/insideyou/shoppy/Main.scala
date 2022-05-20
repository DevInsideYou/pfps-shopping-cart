package dev.insideyou
package shoppy

import cats.effect._
import dev.profunktor.redis4cats.log4cats._
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  override lazy val run: IO[Unit] = {
    implicit val logger = Slf4jLogger.getLogger[IO]

    Program.make[IO]
  }
}
