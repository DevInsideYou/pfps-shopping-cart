package dev.insideyou
package shoppy
package items

import skunk._
import skunk.codec.all._
import skunk.implicits._
import squants.market._

object SQL {
  lazy val itemId: Codec[ItemId] =
    uuid.imap[ItemId](ItemId(_))(_.value)

  lazy val itemName: Codec[ItemName] =
    varchar.imap[ItemName](ItemName(_))(_.value)

  lazy val itemDesc: Codec[ItemDescription] =
    varchar.imap[ItemDescription](ItemDescription(_))(_.value)

  lazy val money: Codec[Money] = numeric.imap[Money](USD(_))(_.amount)

  lazy val decoder: Decoder[Item] = // format: off
    (
      itemId ~
      itemName ~
      itemDesc ~
      money ~
      branding.SQL.brandId ~
      branding.SQL.brandName ~
      categories.SQL.categoryId ~
      categories.SQL.categoryName
    ).map { // format: on
      case i ~ n ~ d ~ p ~ bi ~ bn ~ ci ~ cn =>
        Item(i, n, d, p, branding.Brand(bi, bn), categories.Category(ci, cn))
    }

  lazy val selectAll: Query[Void, Item] =
    sql"""
        SELECT i.uuid, i.name, i.description, i.price, b.uuid, b.name, c.uuid, c.name
        FROM items AS i
        INNER JOIN brands AS b ON i.brand_id = b.uuid
        INNER JOIN categories AS c ON i.category_id = c.uuid
       """.query(decoder)

  lazy val selectByBrand: Query[branding.BrandName, Item] =
    sql"""
        SELECT i.uuid, i.name, i.description, i.price, b.uuid, b.name, c.uuid, c.name
        FROM items AS i
        INNER JOIN brands AS b ON i.brand_id = b.uuid
        INNER JOIN categories AS c ON i.category_id = c.uuid
        WHERE b.name LIKE ${branding.SQL.brandName}
       """.query(decoder)
}
