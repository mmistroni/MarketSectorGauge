package com.mm.marketgauge.persistence.mongo
import com.mm.marketgauge.entities.CompanyRepo
import com.mongodb.casbah.commons.TypeImports.DBObject
import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.CompanyRepoProperties._


trait CompanyRepoRepository extends BaseMongoRepository[DBObject, CompanyRepo] {
  
  override val collectionName = "companiesrepo"
  
  override def insertIndividually(companyRepos:Seq[CompanyRepo]):Int = {
    var updates = 0
    for (companyRepo <- companyRepos) {
      val q = MongoDBObject("ticker" -> companyRepo.ticker)
      
      logger.info(s"Updating ${companyRepo.name}")
      
      val update = MongoDBObject(
          
              "$set" -> MongoDBObject("name" -> companyRepo.name,
                                      "marketCap" -> companyRepo.marketCap,
                                      "sector" -> companyRepo.sector,
                                      "ticker" -> companyRepo.ticker,
                                      "ipoYear" -> companyRepo.ipoYear,
                                      "industry" -> companyRepo.industry)
                )
      client(collectionName).update(q, update, true)
      updates +=1
   }
   updates
  }
  
  
  
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