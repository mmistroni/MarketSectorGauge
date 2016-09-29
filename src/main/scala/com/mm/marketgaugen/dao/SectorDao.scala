package com.mm.marketgaugen.dao

import com.mm.marketgauge.entities.Sector
import com.mongodb.casbah.{MongoClient, MongoClientURI, WriteConcern}
import com.mm.marketgauge.converters.SectorConverter
import com.mongodb.casbah.Imports._

/**
 * User: talg
 */

trait SectorDao {
  /**
   * Mongo URI string [[http://docs.mongodb.org/manual/reference/connection-string/]]
   */
  private val uri = """mongodb://localhost:27017/"""
  val db = MongoClient(MongoClientURI(uri))( """test""")
  val collection = db("sectors")

  def insertBulk(sectors: Seq[Sector]):Int = {
    
    collection.findOne() match {
      case Some(coll) => {
              println("...There's data in db. updating one by one...") 
              insertIndividually(sectors)
            }
      case None => {
          println("Bulk Insert....")
          bulkInsert(sectors)
      }
    }
    
    
  }

  private def insertIndividually(sectors:Seq[Sector]):Int = {
    var updates = 0
    for (sector <- sectors) {
      val q = MongoDBObject("sectorId" -> sector.sectorId)
      
      println(s"Updating ${sector.sectorId}")
      val update = MongoDBObject(
              "$set" -> MongoDBObject("sectorId" -> sector.sectorId,
                                      "name" -> sector.name,
                                      "ticker" -> sector.ticker)
                )
      collection.update(q, update, true)
      updates +=1
   }
   updates
  }
  
  
  private def bulkInsert(sectors:Seq[Sector]):Int = {
    val builder = collection.initializeOrderedBulkOperation
    sectors.foreach(s => builder.insert(SectorConverter.convertToMongoObject(s)))
    builder.execute().insertedCount
    
  }
  
  
  def insertSector(sector: Sector) = {
    println("Inserting:" + sector.name)
    // we shud do a find. if available, copy data, then save
    collection.save(SectorConverter.convertToMongoObject(sector))
    
  }
  
  
  def getAllSectorIds:Seq[Int] = {
    findAll.map(sector => sector.sectorId).toList
  }
  

  def findAll = {
    collection.find.map { item => SectorConverter.convertFromMongoObject(item) }
  }
  
  
}