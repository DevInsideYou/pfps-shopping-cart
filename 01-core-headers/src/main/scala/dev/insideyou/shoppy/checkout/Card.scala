package dev.insideyou
package shoppy
package checkout

import derevo.cats._
import derevo.derive
import eu.timepit.refined.api._
import eu.timepit.refined.boolean.And
import eu.timepit.refined.cats._
import eu.timepit.refined.collection.Size
import eu.timepit.refined.string.{ MatchesRegex, ValidInt }
import io.estatico.newtype.macros.newtype

@derive(show)
final case class Card(
    name: Card.Name,
    number: Card.Number,
    expiration: Card.Expiration,
    cvv: Card.CVV
)

object Card {
  @derive(show)
  @newtype
  final case class Name(value: Name.Pred)
  object Name {
    type Pred = String Refined MatchesRegex["^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$"]
  }

  @derive(show)
  @newtype
  final case class Number(value: Number.Pred)
  object Number {
    type Pred = Long Refined Size[16]
  }

  @derive(show)
  @newtype
  final case class Expiration(value: Expiration.Pred)
  object Expiration {
    type Pred = String Refined (Size[4] And ValidInt)
  }

  @derive(show)
  @newtype
  final case class CVV(value: CVV.Pred)
  object CVV {
    type Pred = Int Refined Size[3]
  }
}
