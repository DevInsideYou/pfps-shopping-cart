package dev.insideyou

trait HasConfig[F[_], Config] {
  def config: F[Config]
}
