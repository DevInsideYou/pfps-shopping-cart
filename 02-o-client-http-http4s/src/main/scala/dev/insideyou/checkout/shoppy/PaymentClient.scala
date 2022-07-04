package dev.insideyou
package shoppy
package checkout

import cats.effect.MonadCancelThrow
import cats.syntax.all._
import eu.timepit.refined.auto._
import org.http4s.Method._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.circe._
import org.http4s.client._
import org.http4s.client.dsl.Http4sClientDsl

import ordering.CirceCodecs._
import CirceCodecs._

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
                  .Payment(Option(st.reason).getOrElse("unknown"))
                  .raiseError[F, ordering.PaymentId]
            }
          }
        }
    }
}
