package dev.insideyou
package shoppy
package health

import health_package_object._

trait Boundary[F[_]] {
  def status: AppStatus
}
