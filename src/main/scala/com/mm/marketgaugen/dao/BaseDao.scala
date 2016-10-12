package com.mm.marketgaugen.dao

import com.mongodb.casbah.{MongoClient, MongoClientURI, WriteConcern}
import com.mm.marketgauge.converters.{CompanyConverter, CompanyRepoConverter}
import com.mongodb.casbah.Imports._

trait BaseDao {
  val database:MongoDatabase  
}

