package dev.insideyou
package shoppy
package branding
package admin

import skunk._
import skunk.implicits._

object SQL {
  lazy val insertBrand: Command[Brand] =
    sql"""
        INSERT INTO brands
        VALUES (${branding.SQL.codec})
        """.command
}
