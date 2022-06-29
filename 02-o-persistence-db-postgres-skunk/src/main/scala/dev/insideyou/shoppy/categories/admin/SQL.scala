package dev.insideyou
package shoppy
package categories
package admin

import skunk._
import skunk.implicits._

object SQL {
  lazy val insertCategory: Command[Category] =
    sql"""
        INSERT INTO categories
        VALUES (${categories.SQL.codec})
        """.command
}
