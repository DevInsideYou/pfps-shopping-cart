package dev.insideyou
package shoppy
package items
package admin

import skunk._
import skunk.implicits._

object SQL {
  import items.SQL._

  lazy val insertItem: Command[ItemId ~ CreateItem] =
    sql"""
        INSERT INTO items
        VALUES ($itemId, $itemName, $itemDesc, $money, ${branding.SQL.brandId}, ${categories.SQL.categoryId})
       """.command.contramap {
      case id ~ i =>
        id ~ i.name ~ i.description ~ i.price ~ i.brandId ~ i.categoryId
    }

  lazy val updateItem: Command[UpdateItem] =
    sql"""
        UPDATE items
        SET price = $money
        WHERE uuid = $itemId
       """.command.contramap(i => i.price ~ i.id)
}
