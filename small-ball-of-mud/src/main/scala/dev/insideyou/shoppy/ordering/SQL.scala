package dev.insideyou
package shoppy
package ordering

import skunk._
import skunk.circe.codec.all._
import skunk.codec.all._
import skunk.implicits._

import items.CirceCodecs._

object SQL {
  lazy val orderId: Codec[OrderId] =
    uuid.imap[OrderId](OrderId(_))(_.value)

  lazy val paymentId: Codec[PaymentId] =
    uuid.imap[PaymentId](PaymentId(_))(_.value)

  lazy val decoder: Decoder[Order] =
    orderTwiddle.map {
      case o ~ _ ~ p ~ i ~ t =>
        Order(o, p, i, t)
    }

  lazy val encoder: Encoder[UserId ~ Order] =
    orderTwiddle.contramap[UserId ~ Order] {
      case id ~ o =>
        o.id ~ id ~ o.paymentId ~ o.items ~ o.total
    }

  private lazy val orderTwiddle = // format: off
    (
      orderId ~
      users.auth.SQL.userId ~
      paymentId ~
      jsonb[Map[items.ItemId, items.Quantity]] ~
      items.SQL.money
    ) // format: on

  lazy val selectByUserIdAndOrderId: Query[UserId ~ OrderId, Order] =
    sql"""
        SELECT * FROM orders
        WHERE user_id = ${users.auth.SQL.userId}
        AND uuid = $orderId
       """.query(decoder)

  lazy val selectByUserId: Query[UserId, Order] =
    sql"""
        SELECT * FROM orders
        WHERE user_id = ${users.auth.SQL.userId}
       """.query(decoder)

  lazy val insertOrder: Command[UserId ~ Order] =
    sql"""
        INSERT INTO orders
        VALUES ($encoder)
       """.command
}
