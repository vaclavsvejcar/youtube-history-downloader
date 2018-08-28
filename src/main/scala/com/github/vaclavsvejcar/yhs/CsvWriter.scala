package com.github.vaclavsvejcar.yhs

import java.io.PrintWriter

import simulacrum.typeclass

class CsvWriter[T: CsvRow](filename: String) {

  val writer = new PrintWriter(filename)
  var headerWritten = false

  def writeRow(data: T): Unit = {
    if (!headerWritten) {
      writer.write(CsvRow[T].header.mkString(",") + "\n")
      headerWritten = true
    }

    writer.write(CsvRow[T].row(data).mkString(",") + "\n")
  }

  def close(): Unit = writer.close()
}

object CsvWriter {
  implicit def autoCloseable[T]: AutoCloseable[CsvWriter[T]] =
    (resource: CsvWriter[T]) => resource.close()
}

@typeclass trait CsvRow[T] {
  def header: Seq[String]

  def row(data: T): Seq[String]
}

@typeclass trait AutoCloseable[T] {
  def close(resource: T)
}
