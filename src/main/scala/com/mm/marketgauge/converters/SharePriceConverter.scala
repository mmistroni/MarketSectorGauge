package com.mm.marketgauge.converters

import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgauge.entities.SharePriceProperties._

object SharePriceConverter {

  def mongoFail = throw new Exception("Unable to access object")
  
  def convertToMongoObject(sharePrice: SharePrice): DBObject = {
    val builder = MongoDBObject.newBuilder
    builder += ID -> sharePrice._id
    builder += TICKER -> sharePrice.ticker
    builder += ASOFDATE -> sharePrice.asOfDate
    builder += PRICE -> sharePrice.price
    builder += CURRENTEPS ->  sharePrice.currentEps 
    builder += FORWARDEPS -> sharePrice.forwardEps
    builder += MOVINGAVG -> sharePrice.movingAverage
    builder += EXDIVDATE -> sharePrice.exDivDate
    builder += PEG -> sharePrice.peg
    builder += SHORTRATIO -> sharePrice.shortRatio
    builder.result()
  }

  def convertFromMongoObject(db: DBObject): SharePrice = {
    val id = db.getAs[ObjectId](ID) orElse {mongoFail}
    val ticker = db.getAs[String](TICKER) orElse{mongoFail}
    val asOfDate = db.getAs[String](ASOFDATE) orElse{mongoFail}
    val price = db.getAs[Double](PRICE) orElse{mongoFail}
    val currentEps = db.getAs[Double](CURRENTEPS) orElse{mongoFail}
    val forwardEps = db.getAs[Double](FORWARDEPS) orElse{mongoFail}
    val movingAverage = db.getAs[Double](MOVINGAVG) orElse{mongoFail}
    val extDivDate = db.getAs[String](EXDIVDATE) orElse{mongoFail}
    val peg = db.getAs[Double](PEG) orElse{mongoFail}
    val shortRatio = db.getAs[Double](SHORTRATIO) orElse{mongoFail}
  
    SharePrice(id.get, ticker.get, price.get, asOfDate.get, 
                currentEps.get, forwardEps.get, movingAverage.get,
                extDivDate.get, peg.get, shortRatio.get)
    
  }
    
    
}
  
  


