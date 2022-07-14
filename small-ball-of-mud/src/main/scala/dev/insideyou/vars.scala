package dev.insideyou

import java.util.UUID

import cats.implicits._

object vars {
  protected class UUIDVar[A](f: UUID => A) {
    def unapply(str: String): Option[A] =
      Either.catchNonFatal(f(UUID.fromString(str))).toOption
  }

  object ItemIdVar  extends UUIDVar(shoppy.items.ItemId.apply)
  object OrderIdVar extends UUIDVar(shoppy.ordering.OrderId.apply)
}
