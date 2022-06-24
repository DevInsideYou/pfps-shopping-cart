package dev.insideyou
package shoppy
package ordering

import cats.effect._
import io.circe.{ Codec => CCodec, Decoder => _, Encoder => _, _ }
import skunk._
import skunk.circe.codec.all._
import skunk.codec.all._
import skunk.implicits._

object StoragePostgresImpl {
  def make[F[_]: MonadCancelThrow](
      postgres: Resource[F, Session[F]]
  )(implicit C: fs2.Compiler[F, F]): Storage[F] =
    new Storage[F] {
      override def getOrder(userId: UserId, orderId: OrderId): F[Option[Order]] =
        postgres.use { session =>
          session.prepare(SQL.selectByUserIdAndOrderId).use { q =>
            q.option(userId ~ orderId)
          }
        }

      override def findOrderBy(userId: UserId): F[List[Order]] =
        postgres.use { session =>
          session.prepare(SQL.selectByUserId).use { q =>
            q.stream(userId, 1024).compile.toList
          }
        }
    }

  object SQL {
    import items.StoragePostgresImpl.SQL._
    import users.auth.StoragePostgresImpl.SQL._

    lazy val orderId: Codec[OrderId] =
      uuid.imap[OrderId](OrderId(_))(_.value)

    lazy val paymentId: Codec[PaymentId] =
      uuid.imap[PaymentId](PaymentId(_))(_.value)

    implicit lazy val itemIdCodec: CCodec[items.ItemId] =
      CCodec.from(items.ItemId.deriving, items.ItemId.deriving)

    implicit lazy val itemIdKeyEncoder: KeyEncoder[items.ItemId] =
      items.ItemId.deriving

    implicit lazy val itemIdKeyDecoder: KeyDecoder[items.ItemId] =
      items.ItemId.deriving

    implicit lazy val quantityIdCodec: CCodec[items.Quantity] =
      CCodec.from(items.Quantity.deriving, items.Quantity.deriving)

    lazy val decoder: Decoder[Order] =
      (orderId ~ userId ~ paymentId ~ jsonb[Map[items.ItemId, items.Quantity]] ~ money).map {
        case o ~ _ ~ p ~ i ~ t =>
          Order(o, p, i, t)
      }

    lazy val encoder: Encoder[UserId ~ Order] =
      (orderId ~ userId ~ paymentId ~ jsonb[Map[items.ItemId, items.Quantity]] ~ money)
        .contramap[UserId ~ Order] {
          case id ~ o =>
            o.id ~ id ~ o.paymentId ~ o.items ~ o.total
        }

    lazy val selectByUserIdAndOrderId: Query[UserId ~ OrderId, Order] =
      sql"""
        SELECT * FROM orders
        WHERE user_id = $userId
        AND uuid = $orderId

       """.query(decoder)

    lazy val selectByUserId: Query[UserId, Order] =
      sql"""
        SELECT * FROM orders
        WHERE user_id = $userId
       """.query(decoder)

    val insertOrder: Command[UserId ~ Order] =
      sql"""
        INSERT INTO orders
        VALUES ($encoder)
       """.command
  }
}
