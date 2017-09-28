package com.mm.marketgauge.persistence.mongo
import com.mm.marketgauge.entities.Sector
import com.mongodb.casbah.commons.TypeImports.DBObject
import com.mm.marketgauge.persistence.BaseSectorRepository
import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.SectorProperties._


trait SectorRepository extends BaseMongoRepository[Sector] {
  
  override val collectionName = "sectors"
  
  def convertFromMongoObject(mongoObject:DBObject) = {
    val id = mongoObject.getAs[ObjectId](ID) //orElse {mongoFail(ID)}
    val ticker = mongoObject.getAs[String](TICKER) orElse{mongoFail(id + "@" + TICKER)}
    val name = mongoObject.getAs[String](NAME) orElse{mongoFail(id + "@" + NAME)}
    val sectorId = mongoObject.getAs[Integer](SECTORID) orElse{mongoFail(id + "@" + SECTORID)}
    val source = mongoObject.getAs[String](SOURCE) 
    Sector(name.get, ticker.get, sectorId.get, 
                source.getOrElse(""))
    
  }
  
  def convertToMongoObject(entity:Sector) = {
    val builder = MongoDBObject.newBuilder
    builder += ID -> null
    builder += TICKER -> entity.ticker
    builder += NAME -> entity.name
    builder += SECTORID -> entity.sectorId
    builder += SOURCE ->  entity.source 
    builder.result()
    
  }
  
  
  
  override def insertIndividually(sectors:Seq[Sector]):Int = {
    var updates = 0
    for (sector <- sectors) {
      val q = MongoDBObject("sectorId" -> sector.sectorId)
      val update = MongoDBObject(
              "$set" -> MongoDBObject("sectorId" -> sector.sectorId,
                                      "name" -> sector.name,
                                      "ticker" -> sector.ticker)
                )
      client(collectionName).update(q, update, true)
      updates +=1
   }
   updates
  }
  
  def getAllSectors = getAll
  
}