package com.mm.marketgauge.dao

import com.mongodb.casbah.{MongoClient, MongoClientURI, WriteConcern}
import com.mongodb.casbah.Imports._

trait BaseDao {
  val database:MongoDatabase  
}

