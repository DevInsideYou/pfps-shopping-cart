package dev.insideyou

object configDecoder extends Derive[Decoder.Id]

object Decoder {
  type Id[A] = ciris.ConfigDecoder[String, A]
}
