package dev.insideyou
package shoppy
package ordering

import dev.insideyou.{ shoppy => domain }
import squants.market.Money

final case class Order(
    id: OrderId,
    paymentId: PaymentId,
    items: Map[domain.items.ItemId, domain.items.Quantity],
    total: Money
)
