package dev.insideyou
package shoppy
package checkout

import eu.timepit.refined._
import eu.timepit.refined.api._
import eu.timepit.refined.auto._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.Size
import eu.timepit.refined.string.ValidInt
import io.circe.Decoder
import io.circe.magnolia.derivation.decoder.semiauto._
import io.circe.refined._

object CirceCodecs {
  implicit lazy val jsonDecoderForName: Decoder[Card.Name] =
    Card.Name.deriving

  implicit lazy val jsonDecoderForNumber: Decoder[Card.Number] =
    decoderOf[Long, Size[16]].map(Card.Number(_))

  implicit lazy val jsonDecoderForExpiration: Decoder[Card.Expiration] =
    decoderOf[String, Size[4] And ValidInt].map(Card.Expiration(_))

  implicit lazy val jsonDecoderForCVV: Decoder[Card.CVV] =
    decoderOf[Int, Size[3]].map(Card.CVV(_))

  implicit lazy val jsonDecoderForCard: Decoder[Card] =
    deriveMagnoliaDecoder

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
