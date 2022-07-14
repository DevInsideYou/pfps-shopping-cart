package dev.insideyou
package shoppy
package health

import io.circe._
import derevo.circe.magnolia._

import health_package_object._

object CirceCodecs {
  implicit lazy val jsonCodec: Codec[health_package_object.Status] =
    Codec.from(decoder.instance, Encoder.forProduct1("status")(_.toString))

  implicit lazy val encoderForRedisStatus: Codec[RedisStatus] =
    Codec.from(RedisStatus.deriving, RedisStatus.deriving)

  implicit lazy val encoderForPostgresStatus: Codec[PostgresStatus] =
    Codec.from(PostgresStatus.deriving, PostgresStatus.deriving)

  implicit lazy val encoderForAppStatus: Codec[AppStatus] =
    Codec.from(decoder.instance, encoder.instance)
}
