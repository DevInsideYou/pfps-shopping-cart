package dev.insideyou
package shoppy
package items
package admin

import squants.market.Money

final case class CreateItem(
    name: ItemName,
    description: ItemDescription,
    price: Money,
    brandId: branding.BrandId,
    categoryId: categories.CategoryId
)
