package dev.insideyou.shoppy.categories

import skunk._
import skunk.codec.all._
import skunk.implicits._

object SQL {
  lazy val categoryId: Codec[CategoryId] =
    uuid.imap[CategoryId](CategoryId(_))(_.value)

  lazy val categoryName: Codec[CategoryName] =
    varchar.imap[CategoryName](CategoryName(_))(_.value)

  lazy val codec: Codec[Category] =
    (categoryId ~ categoryName).imap {
      case i ~ n => Category(i, n)
    }(b => b.uuid ~ b.name)

  lazy val selectAll: Query[Void, Category] =
    sql"""
        SELECT * FROM categories
       """.query(codec)
}
