package dev.insideyou
package shoppy

import cats._
import cats.effect._
import cats.syntax.all._
import dev.profunktor.redis4cats.effect._
import org.typelevel.log4cats._

object ResourcesLoader {
  def load[F[_]: MkRedis: Logger: Async: std.Console: NonEmptyParallel](
      appEnvironment: AppEnvironment
  ): F[Resource[F, Resources[F]]] =
    (
      RedisSessionLoader.load(appEnvironment),
      PostgresSessionLoader.load
    ).parTupled.map(_.parMapN(Resources.apply))
}
