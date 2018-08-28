package com.github.vaclavsvejcar.yhs.tools

import simulacrum.typeclass

@typeclass trait Resource[T] {
  def close(resource: T)
}