package com.mm.marketgaugen.dao

private [dao] trait MongoDatabase extends Database with com.mm.marketgauge.util.LogHelper {
  import com.mongodb.casbah.{MongoClient, MongoClientURI}
  logger.info(s"Initalizing wiht:$uri")
  lazy val client = MongoClient(MongoClientURI(uri))(databaseName)
  
}
