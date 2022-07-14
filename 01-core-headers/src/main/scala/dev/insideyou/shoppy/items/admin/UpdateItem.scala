package dev.insideyou
package shoppy
package items
package admin

import squants.market.Money

final case class UpdateItem(
    id: ItemId,
    price: Money
)
