package dev.insideyou
package shoppy
package health

import skunk._
import skunk.codec.all._
import skunk.implicits._

object SQL {
  lazy val query: Query[Void, Int] =
    sql"SELECT pid FROM pg_stat_activity".query(int4)
}
