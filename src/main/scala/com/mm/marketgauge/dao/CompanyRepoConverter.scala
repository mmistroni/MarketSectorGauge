package com.mm.marketgauge.dao

import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.CompanyRepo
import com.mm.marketgauge.entities.CompanyRepoProperties._

object CompanyRepoConverter {

  def mongoFail(field:String) = throw new Exception(s"Unable to access object for:$field")
  
  def convertToMongoObject(company: CompanyRepo): DBObject = {
    val builder = MongoDBObject.newBuilder
    
    builder += ID -> company._id
    builder += TICKER -> company.ticker
    builder += NAME -> company.name
    builder += LASTSALE -> company.lastSale
    builder += MARKETCAP ->  company.marketCap 
    builder += IPOYEAR -> company.ipoYear
    builder += SECTOR -> company.sector
    builder += INDUSTRY ->  company.industry 
    builder.result()
    
  }

  def convertFromMongoObject(db: DBObject): CompanyRepo = {
    val id = db.getAs[ObjectId](ID) orElse {mongoFail(ID)}
    val ticker = db.getAs[String](TICKER) orElse{mongoFail(TICKER)}
    val name = db.getAs[String](NAME) orElse{mongoFail(NAME)}
    val lastSale = db.getAs[Double](LASTSALE) orElse{mongoFail(LASTSALE)}
    val marketCap = db.getAs[String](MARKETCAP) orElse{mongoFail(MARKETCAP)}
    val ipoYear = db.getAs[String](IPOYEAR) orElse{mongoFail(IPOYEAR)}
    val sector = db.getAs[String](SECTOR) orElse{mongoFail(SECTOR)}
    val industry = db.getAs[String](INDUSTRY) orElse{mongoFail(INDUSTRY)}
    
    CompanyRepo(id.get, ticker.get, name.get, lastSale.get, 
                marketCap.get, ipoYear.get, sector.get, industry.get)
   
  }
    
    
}
  
  


