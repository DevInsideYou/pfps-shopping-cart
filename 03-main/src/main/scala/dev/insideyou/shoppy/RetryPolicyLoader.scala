package dev.insideyou
package shoppy

import scala.concurrent.duration._

import cats._
import cats.syntax.all._
import retry._

object RetryPolicyLoader {
  def load[F[_]: Applicative]: F[RetryPolicy[F]] =
    List(
      RetryPolicies.limitRetries(3),
      RetryPolicies.exponentialBackoff(10.milliseconds)
    ).reduceLeft(_ combine _).pure
}
