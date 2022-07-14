package dev.insideyou

import org.typelevel.log4cats

object LoggerImpl {
  def make[F[_]: log4cats.Logger](moduleName: String): Logger[F] =
    new Logger[F] {
      override def error(message: => String): F[Unit] = l.error(message)
      override def warn(message: => String): F[Unit]  = l.warn(message)
      override def info(message: => String): F[Unit]  = l.info(message)
      override def debug(message: => String): F[Unit] = l.debug(message)
      override def trace(message: => String): F[Unit] = l.trace(message)

      override def error(t: Throwable)(message: => String): F[Unit] = l.error(t)(message)
      override def warn(t: Throwable)(message: => String): F[Unit]  = l.warn(t)(message)
      override def info(t: Throwable)(message: => String): F[Unit]  = l.info(t)(message)
      override def debug(t: Throwable)(message: => String): F[Unit] = l.debug(t)(message)
      override def trace(t: Throwable)(message: => String): F[Unit] = l.trace(t)(message)

      private lazy val l =
        log4cats.Logger[F].withModifiedString(_.prependedAll(s"[$moduleName] "))
    }
}

object HasLoggerImpl {
  def make[F[_]: log4cats.Logger](moduleName: String): HasLogger[F] =
    make(LoggerImpl.make(moduleName))

  def make[F[_]](l: Logger[F]): HasLogger[F] =
    new HasLogger[F] {
      override lazy val logger: Logger[F] =
        l
    }
}
