package com.mm.marketgauge.converters

import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.entities.SectorProperties._

object SectorConverter extends com.mm.marketgauge.util.LogHelper {

  def mongoFail = throw new Exception("Unable to access object")
  
  def convertToMongoObject(sector: Sector): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += ID -> sector._id
    builder += TICKER -> sector.ticker
    builder += NAME -> sector.name
    builder += SECTORID -> sector.sectorId
    builder += SOURCE ->  sector.source 
    builder.result()
    
  }

  def convertFromMongoObject(db: DBObject): Sector = {
    logger.info("Converting for ticker:"+ db.getAs[String](TICKER) + " " + db.getAs[String](NAME))
    val id = db.getAs[ObjectId](ID) orElse {mongoFail}
    val ticker = db.getAs[String](TICKER) orElse{mongoFail}
    val name = db.getAs[String](NAME) orElse{mongoFail}
    val sectorId = db.getAs[Integer](SECTORID) orElse{mongoFail}
    val source = db.getAs[String](SOURCE) 
    Sector(id.get, name.get, ticker.get, sectorId.get, 
                source.get)
    
  }
    
    
}
  
  


