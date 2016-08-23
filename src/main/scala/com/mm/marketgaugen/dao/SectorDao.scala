package com.mm.marketgaugen.dao

import com.mm.marketgauge.entities.Sector
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import com.mm.marketgauge.converters.SectorConverter
import com.mongodb.casbah.Imports._

/**
 * User: talg
 */

object SectorDao {
  /**
   * Mongo URI string [[http://docs.mongodb.org/manual/reference/connection-string/]]
   */
  private val uri = """mongodb://localhost:27017/"""
  val db = MongoClient(MongoClientURI(uri))( """test""")
  val collection = db("sectors")

  def insertBulk(sectors: Seq[Sector]) = {
    
    null
  }

  

  def findAll = {
    collection.find.map { item => SectorConverter.convertFromMongoObject(item) }
    
  }
  
  
}