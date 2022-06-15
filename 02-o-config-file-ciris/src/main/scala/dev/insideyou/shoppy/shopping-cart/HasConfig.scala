package dev.insideyou
package shoppy
package shopping_cart

import scala.concurrent.duration._

import cats._
import cats.syntax.all._

object HasConfigImpl {
  def make[F[_]: Applicative]: HasConfig[F, Config] =
    new HasConfig[F, Config] {
      override lazy val config: F[Config] =
        Config(ShoppingCartExpiration(30.minutes)).pure
    }
}
