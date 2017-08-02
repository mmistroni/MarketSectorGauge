package com.mm.marketgauge.service

import amazon.util.AWSClientFactory
import com.typesafe.config._

/**
 * Notifier // Use a Component so  you dont need a Factory
 */
trait Notifier {
  /**
   * notify recipients 
   * @param message: a String representing a message
   */
  def notify(subject:String, message:String):Unit
}


private[service] class AWSNotifier(topic:String) extends Notifier {
  private [service] val snsClient = AWSClientFactory.snsClient
  def notify(subject:String, message:String) = snsClient.publishToTopic(topic, subject, message)
}


object Notifier {
  def defaultNotifier(conf:Config) = new AWSNotifier("arn:aws:sns:us-west-2:049597339122:MarketSectorApp")
}