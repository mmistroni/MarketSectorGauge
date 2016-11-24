package com.mm.marketgauge.dao

import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.Company
import com.mm.marketgauge.entities.CompanyProperties._

object CompanyConverter {

  def mongoFail = throw new Exception("Unable to access object")
  
  def convertToMongoObject(company: Company): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += ID -> company._id
    builder += NAME -> company.name
    builder += PRICECHANGE -> company.priceChange
    builder += MARKETCAP -> company.marketCap
    builder += PRICETOEARNINGS -> company.priceToEarnings
    builder += ROE -> company.roe
    builder += DIVYIELD -> company.divYield
    builder += LTDTOEQ -> company.ltDToEq
    builder += PRICETOBOOK -> company.priceToBook
    builder += NETPROFMGN -> company.netProfMgn
    builder += PRICETOCASHFLOW -> company.priceToCashFlow
    builder += SECTORID -> company.sectorId
  
    builder.result()
    
  }

  def convertFromMongoObject(db: DBObject): Company = {
    
    val id = db.getAs[ObjectId](ID) orElse {mongoFail}
    val name = db.getAs[String](NAME) orElse{mongoFail}
    val priceChange = db.getAs[Double](PRICECHANGE) orElse{mongoFail}
    val marketCap = db.getAs[Double](MARKETCAP) orElse{mongoFail}
    val priceToEarnings = db.getAs[Double](PRICETOEARNINGS) orElse{mongoFail}
    val roe = db.getAs[Double](ROE) orElse{mongoFail}
    val divYield = db.getAs[Double](DIVYIELD) orElse{mongoFail}
    val ltdToEq = db.getAs[Double](LTDTOEQ) orElse{mongoFail}
    val priceToBook = db.getAs[Double](PRICETOBOOK) orElse{mongoFail}
    val netProfMgn = db.getAs[Double](NETPROFMGN) orElse{mongoFail}
    val priceToCashflow = db.getAs[Double](PRICETOCASHFLOW) orElse{mongoFail}
    val sectorId = db.getAs[Int](SECTORID) orElse{mongoFail}
    
    Company(id.get, name.get, priceChange.get, marketCap.get, 
            priceToEarnings.get, roe.get, divYield.get, ltdToEq.get,
            priceToBook.get, netProfMgn.get, priceToCashflow.get, sectorId.get)
    
  }
    
    
}
  
  


