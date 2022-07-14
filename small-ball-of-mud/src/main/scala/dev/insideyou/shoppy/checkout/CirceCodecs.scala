package dev.insideyou
package shoppy
package checkout

import derevo.circe.magnolia._
import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.Size
import eu.timepit.refined.string.ValidInt
import io.circe._
import io.circe.refined._

import items.CirceCodecs._
import users.auth.CirceCodecs._

object CirceCodecs {
  implicit lazy val paymentCodec: Codec[Payment] =
    Codec.from(decoder.instance, encoder.instance)

  implicit lazy val cardNameCodec: Codec[Card.Name] =
    Codec.from(Card.Name.deriving, Card.Name.deriving)

  implicit lazy val cardNumberCodec: Codec[Card.Number] =
    Codec.from(
      decoderOf[Long, Size[16]].map(Card.Number(_)),
      Card.Number.deriving
    )

  implicit lazy val cardExpirationCodec: Codec[Card.Expiration] =
    Codec.from(
      decoderOf[String, Size[4] And ValidInt].map(Card.Expiration(_)),
      Card.Expiration.deriving
    )

  implicit lazy val cardCVVCodec: Codec[Card.CVV] =
    Codec.from(
      decoderOf[Int, Size[3]].map(Card.CVV(_)),
      Card.CVV.deriving
    )

  implicit lazy val cardCodec: Codec[Card] =
    Codec.from(decoder.instance, encoder.instance)

  private def decoderOf[T, P](implicit v: Validate[T, P], d: Decoder[T]): Decoder[T Refined P] =
    d.emap(refineV[P].apply[T](_))

  private implicit def validateSizeN[N <: Int, R](
      implicit w: ValueOf[N]
  ): Validate.Plain[R, Size[N]] =
    Validate.fromPredicate[R, Size[N]](
      _.toString.size == w.value,
      _ => s"Must have ${w.value} digits",
      Size[N](w.value)
    )
}
