package dev.insideyou
package shoppy
package checkout

import squants.market.Money

final case class Payment(
    id: UserId,
    total: Money,
    card: Card
)
