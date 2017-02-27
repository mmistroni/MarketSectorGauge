package com.mm.marketgauge.dao

import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.Company
import com.mm.marketgauge.entities.CompanyProperties._

object CompanyConverter {

  def mongoFail(field:String) = throw new Exception(s"Unable to access object for:$field")
  
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
    
    val id = db.getAs[ObjectId](ID) orElse {mongoFail(ID)}
    val name = db.getAs[String](NAME) orElse{mongoFail(NAME)}
    val priceChange = db.getAs[Double](PRICECHANGE) orElse{mongoFail(PRICECHANGE)}
    val marketCap = db.getAs[Double](MARKETCAP) orElse{mongoFail(MARKETCAP)}
    val priceToEarnings = db.getAs[Double](PRICETOEARNINGS) orElse{mongoFail(PRICETOEARNINGS)}
    val roe = db.getAs[Double](ROE) orElse{mongoFail(ROE)}
    val divYield = db.getAs[Double](DIVYIELD) orElse{mongoFail(DIVYIELD)}
    val ltdToEq = db.getAs[Double](LTDTOEQ) orElse{mongoFail(LTDTOEQ)}
    val priceToBook = db.getAs[Double](PRICETOBOOK) orElse{mongoFail(PRICETOBOOK)}
    val netProfMgn = db.getAs[Double](NETPROFMGN) orElse{mongoFail(NETPROFMGN)}
    val priceToCashflow = db.getAs[Double](PRICETOCASHFLOW) orElse{mongoFail(PRICETOCASHFLOW)}
    val sectorId = db.getAs[Int](SECTORID) orElse{mongoFail(SECTORID)}
    
    Company(id.get, name.get, priceChange.get, marketCap.get, 
            priceToEarnings.get, roe.get, divYield.get, ltdToEq.get,
            priceToBook.get, netProfMgn.get, priceToCashflow.get, sectorId.get)
    
  }
    
    
}
  
  


