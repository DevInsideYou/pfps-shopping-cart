package dev.insideyou

import java.util.UUID

import monocle.Iso

trait IsUUID[A] {
  def _UUID: Iso[UUID, A]
}

object IsUUID {
  def apply[A: IsUUID]: IsUUID[A] = implicitly

  implicit lazy val identityUUID: IsUUID[UUID] = new IsUUID[UUID] {
    lazy val _UUID = Iso[UUID, UUID](identity)(identity)
  }
}

object uuid extends Derive[IsUUID]
