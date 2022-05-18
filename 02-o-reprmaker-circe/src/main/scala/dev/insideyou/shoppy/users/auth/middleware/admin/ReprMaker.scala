package dev.insideyou
package shoppy
package users
package auth
package middleware
package admin

import cats.ApplicativeThrow
import io.circe.Decoder
import io.circe.parser.decode

object ReprMakerImpl {
  def make[F[_]: ApplicativeThrow]: ReprMaker[F] =
    new ReprMaker[F] {
      override def content(rawContent: String): F[ClaimContent] =
        ApplicativeThrow[F].fromEither(decode[ClaimContent](rawContent))
    }

  private implicit lazy val jsonDecoder: Decoder[ClaimContent] =
    Decoder.forProduct1("uuid")(ClaimContent.apply)
}
