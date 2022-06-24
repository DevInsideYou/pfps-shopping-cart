package dev.insideyou
package shoppy
package checkout

import cats.effect.MonadCancelThrow
import cats.syntax.all._
import eu.timepit.refined.auto._
import io.circe._
import io.circe.magnolia.derivation.encoder.semiauto._
import io.circe.refined._
import org.http4s.Method._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl
import squants.market.Money

object PaymentClientImpl {
  def make[F[_]: JsonDecoder: MonadCancelThrow](
      cfg: PaymentConfig,
      client: Client[F]
  ): PaymentClient[F] =
    new PaymentClient[F] with Http4sClientDsl[F] {
      override def processPayment(payment: Payment): F[ordering.PaymentId] =
        Uri.fromString(cfg.uri.value + "/payments").liftTo[F].flatMap { uri =>
          client.run(POST(payment, uri)).use { resp =>
            resp.status match {
              case Status.Ok | Status.Conflict =>
                resp.asJsonDecode[ordering.PaymentId]

              case st =>
                dev.insideyou.shoppy.checkout.Error
                  .Payment(
                    Option(st.reason).getOrElse("unknown")
                  )
                  .raiseError[F, ordering.PaymentId]
            }
          }
        }
    }

  implicit lazy val paymentIdCodec: Codec[ordering.PaymentId] =
    Codec.from(ordering.PaymentId.deriving, ordering.PaymentId.deriving)

  implicit lazy val userIdEncoder: Encoder[UserId] =
    UserId.deriving

  implicit lazy val moneyCodec: Encoder[Money] =
    Encoder[BigDecimal].contramap(_.amount)

  implicit lazy val paymentEncoder: Encoder[Payment] =
    deriveMagnoliaEncoder

  implicit lazy val cardNameEncoder: Encoder[Card.Name] =
    Card.Name.deriving

  implicit lazy val cardNumberEncoder: Encoder[Card.Number] =
    Card.Number.deriving

  implicit lazy val cardExpirationEncoder: Encoder[Card.Expiration] =
    Card.Expiration.deriving

  implicit lazy val cardCVVEncoder: Encoder[Card.CVV] =
    Card.CVV.deriving

  implicit lazy val cardEncoder: Encoder[Card] =
    deriveMagnoliaEncoder
}
