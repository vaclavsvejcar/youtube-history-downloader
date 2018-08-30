package com.github.vaclavsvejcar.yhd.tools

import simulacrum.typeclass

@typeclass trait Resource[T] {
  def close(resource: T)
}