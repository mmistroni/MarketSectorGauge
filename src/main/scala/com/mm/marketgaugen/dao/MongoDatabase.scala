package com.mm.marketgaugen.dao

private [dao] trait MongoDatabase extends Database {
  import com.mongodb.casbah.{MongoClient, MongoClientURI}
  lazy val client = MongoClient(MongoClientURI(uri))(databaseName)
  
}
