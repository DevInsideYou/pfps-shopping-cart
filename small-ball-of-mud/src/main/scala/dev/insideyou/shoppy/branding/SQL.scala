package dev.insideyou
package shoppy
package branding

import skunk._
import skunk.codec.all._
import skunk.implicits._

object SQL {
  lazy val brandId: Codec[BrandId] =
    uuid.imap[BrandId](BrandId(_))(_.value)

  lazy val brandName: Codec[BrandName] =
    varchar.imap[BrandName](BrandName(_))(_.value)

  lazy val codec: Codec[Brand] =
    (brandId ~ brandName).imap {
      case i ~ n => Brand(i, n)
    }(b => b.uuid ~ b.name)

  lazy val selectAll: Query[Void, Brand] =
    sql"""
        SELECT * FROM brands
       """.query(codec)
}
