package dev.insideyou
package shoppy
package shopping_cart

import skunk._
import skunk.implicits._

object SQL {
  lazy val selectById: Query[items.ItemId, items.Item] =
    sql"""
      SELECT i.uuid, i.name, i.description, i.price, b.uuid, b.name, c.uuid, c.name
      FROM items AS i
      INNER JOIN brands AS b ON i.brand_id = b.uuid
      INNER JOIN categories AS c ON i.category_id = c.uuid
      WHERE i.uuid = ${items.SQL.itemId}
     """.query(items.SQL.decoder)
}
