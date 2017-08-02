package com.mm.marketgauge.dao
import com.mm.marketgauge.util.AppConfig
import com.mongodb.casbah.{MongoClient, MongoClientURI}
  

class MongoDatabase extends Database with com.mm.marketgauge.util.LogHelper {
  
  val username = AppConfig.DbConfig.username
  val password = AppConfig.DbConfig.password
  val uri = AppConfig.DbConfig.uri
  val databaseName = AppConfig.DbConfig.dbName
  logger.info(s"Initalizing wiht:$uri and $databaseName")
  lazy val client = MongoClient(MongoClientURI(uri))(databaseName)
  
}
