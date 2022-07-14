package dev.insideyou

import cats._
import cats.syntax.all._

trait HasLogger[F[_]] {
  def logger: Logger[F]
}

trait Logger[F[_]] {
  def error(message: => String): F[Unit]
  def warn(message: => String): F[Unit]
  def info(message: => String): F[Unit]
  def debug(message: => String): F[Unit]
  def trace(message: => String): F[Unit]

  def error(t: Throwable)(message: => String): F[Unit]
  def warn(t: Throwable)(message: => String): F[Unit]
  def info(t: Throwable)(message: => String): F[Unit]
  def debug(t: Throwable)(message: => String): F[Unit]
  def trace(t: Throwable)(message: => String): F[Unit]
}

object Logger {
  def apply[F[_]: Logger]: Logger[F] = implicitly

  def silent[F[_]: Applicative]: Silent[F] = new Silent[F]

  /** We want a class so that we can override parts of it in tests. */
  class Silent[F[_]: Applicative] extends Logger[F] {
    override def error(message: => String): F[Unit] = silence
    override def warn(message: => String): F[Unit]  = silence
    override def info(message: => String): F[Unit]  = silence
    override def debug(message: => String): F[Unit] = silence
    override def trace(message: => String): F[Unit] = silence

    override def error(t: Throwable)(message: => String): F[Unit] = silence
    override def warn(t: Throwable)(message: => String): F[Unit]  = silence
    override def info(t: Throwable)(message: => String): F[Unit]  = silence
    override def debug(t: Throwable)(message: => String): F[Unit] = silence
    override def trace(t: Throwable)(message: => String): F[Unit] = silence

    private lazy val silence: F[Unit] = Applicative[F].unit
  }

  def echo[F[_]: Applicative: Defer]: Echo[F] = new Echo[F]

  /** We want a class so that we can override parts of it in tests. */
  class Echo[F[_]: Applicative: Defer] extends Logger[F] {
    override def error(message: => String): F[Unit] = bad(message)
    override def warn(message: => String): F[Unit]  = good(message)
    override def info(message: => String): F[Unit]  = good(message)
    override def debug(message: => String): F[Unit] = good(message)
    override def trace(message: => String): F[Unit] = good(message)

    override def error(t: Throwable)(message: => String): F[Unit] = bad(t, message)
    override def warn(t: Throwable)(message: => String): F[Unit]  = good(t, message)
    override def info(t: Throwable)(message: => String): F[Unit]  = good(t, message)
    override def debug(t: Throwable)(message: => String): F[Unit] = good(t, message)
    override def trace(t: Throwable)(message: => String): F[Unit] = good(t, message)

    private def good(message: => String): F[Unit] =
      Defer[F].defer {
        Applicative[F].pure {
          println(message)
        }
      }

    private def bad(message: => String): F[Unit] =
      Defer[F].defer {
        Applicative[F].pure {
          Console.err.println(message)
        }
      }

    private def good(t: Throwable, message: => String): F[Unit] =
      Defer[F].defer {
        Applicative[F].pure {
          println(message)

          t.printStackTrace()
        }
      }

    private def bad(t: Throwable, message: => String): F[Unit] =
      Defer[F].defer {
        Applicative[F].pure {
          Console.err.println(message)

          t.printStackTrace(Console.err)
        }
      }
  }

  def styled[F[_]: Applicative: Defer]: Styled[F] = new Styled[F]

  /** We want a class so that we can override parts of it in tests. */
  class Styled[F[_]: Applicative: Defer] extends Logger[F] {
    import Console._

    override def error(message: => String): F[Unit] = bad(message)
    override def warn(message: => String): F[Unit]  = good(message, style = YELLOW.some)
    override def info(message: => String): F[Unit]  = good(message, style = None)
    override def debug(message: => String): F[Unit] = good(message, style = BLUE.some)
    override def trace(message: => String): F[Unit] = good(message, style = GREEN.some)

    override def error(t: Throwable)(message: => String): F[Unit] =
      bad(t, message)

    override def warn(t: Throwable)(message: => String): F[Unit] =
      good(t, message, style = YELLOW.some)

    override def info(t: Throwable)(message: => String): F[Unit] =
      good(t, message, style = None)

    override def debug(t: Throwable)(message: => String): F[Unit] =
      good(t, message, style = BLUE.some)

    override def trace(t: Throwable)(message: => String): F[Unit] =
      good(t, message, style = GREEN.some)

    private def good(message: => String, style: Option[String]): F[Unit] =
      Defer[F].defer {
        Applicative[F].pure {
          println(style.fold(message)(_ + message + RESET))
        }
      }

    private def bad(message: => String): F[Unit] =
      Defer[F].defer {
        Applicative[F].pure {
          Console.err.println(RED + message + RESET)
        }
      }

    private def good(
        t: Throwable,
        message: => String,
        style: Option[String]
    ): F[Unit] =
      Defer[F].defer {
        Applicative[F].pure {
          style.fold {
            println(message)

            t.printStackTrace()
          } { style =>
            println(style + message + RESET)

            Console.out.synchronized {
              print(style)
              t.printStackTrace()
              print(RESET)
            }
          }
        }
      }

    private def bad(t: Throwable, message: => String): F[Unit] =
      Defer[F].defer {
        Applicative[F].pure {
          Console.err.println(RED + message + RESET)

          Console.err.synchronized {
            Console.err.print(RED)
            t.printStackTrace(Console.err)
            Console.err.print(RESET)
          }
        }
      }
  }
}
