package com.mm.marketgauge.util
import grizzled.slf4j.Logger


trait LogHelper {
  val loggerName = this.getClass.getName
  lazy val logger = Logger(loggerName)
}