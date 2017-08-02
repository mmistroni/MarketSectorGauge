package com.mm.marketgauge.util
import com.typesafe.config.ConfigFactory

object AppConfig {
  
  private val config =  ConfigFactory.load()

  private lazy val root = config.getConfig("my_app")
  
  lazy val shares = config.getConfig("custom_shares")

  object DbConfig {
    private val dbConfig = config.getConfig("db")

    lazy val uri = dbConfig.getString("uri")
    lazy val username = dbConfig.getString("username")
    lazy val password = dbConfig.getString("password")
    lazy val dbName = dbConfig.getString("name")
  }
}