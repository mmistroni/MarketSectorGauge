package com.mm.marketgauge.dao

import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.entities.SectorProperties._

object SectorConverter extends com.mm.marketgauge.util.LogHelper {

  def mongoFail(field:String) = throw new Exception(s"Unable to access object for :$field")
  
  def convertToMongoObject(sector: Sector): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += ID -> null
    builder += TICKER -> sector.ticker
    builder += NAME -> sector.name
    builder += SECTORID -> sector.sectorId
    builder += SOURCE ->  sector.source 
    builder.result()
    
  }

  def convertFromMongoObject(db: DBObject): Sector = {
    val id = db.getAs[ObjectId](ID) orElse {mongoFail(ID)}
    val ticker = db.getAs[String](TICKER) orElse{mongoFail(id + "@" + TICKER)}
    val name = db.getAs[String](NAME) orElse{mongoFail(id + "@" + NAME)}
    val sectorId = db.getAs[Integer](SECTORID) orElse{mongoFail(id + "@" + SECTORID)}
    val source = db.getAs[String](SOURCE) 
    Sector(name.get, ticker.get, sectorId.get, 
                source.getOrElse(""))
    
  }
    
    
}
  
  


