package com.github.vaclavsvejcar.yhd.core.tools

import simulacrum.typeclass

@typeclass trait Resource[T] {
  def close(resource: T)
}