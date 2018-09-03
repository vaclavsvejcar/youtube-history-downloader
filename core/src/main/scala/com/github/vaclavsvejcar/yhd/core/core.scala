package com.github.vaclavsvejcar.yhd

import wvlet.log.LogSupport

package object core extends LogSupport {

  def abort(message: String, throwable: Throwable): Nothing = {
    error(message, throwable)
    System.exit(1)
    throw throwable
  }
}
