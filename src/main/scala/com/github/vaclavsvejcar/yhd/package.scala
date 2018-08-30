package com.github.vaclavsvejcar

import wvlet.log.LogSupport

package object yhd extends LogSupport {

  def abort(message: String, throwable: Throwable): Nothing = {
    error(message, throwable)
    System.exit(1)
    throw throwable
  }
}
