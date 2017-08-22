package com.mm.marketgauge.persistence.mongo
import com.mm.marketgauge.entities.SharePrice
import com.mongodb.casbah.commons.TypeImports.DBObject
import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.SharePriceProperties._


trait SharePriceRepository extends BaseMongoRepository[DBObject, SharePrice] {
  
  override val collectionName = "share_prices"
  
  override def insert(all:Seq[SharePrice]) = bulkInsert(all)
  
  override def insertIndividually(prices:Seq[SharePrice]) = 0
  
  
  def convertFromMongoObject(mongoObject:DBObject) = {
    // only Id, ticker, asOfDate and price are required
    val id = mongoObject.getAs[ObjectId](ID) orElse {mongoFail(ID)}
    val ticker = mongoObject.getAs[String](TICKER) orElse{mongoFail(TICKER)}
    val asOfDate = mongoObject.getAs[String](ASOFDATE) orElse{mongoFail(ASOFDATE)}
    val price = mongoObject.getAs[Double](PRICE) orElse{mongoFail(PRICE)}
    val currentEps = mongoObject.getAs[Double](CURRENTEPS) orElse{Option(Double.NaN)}
    val forwardEps = mongoObject.getAs[Double](FORWARDEPS) orElse{Option(Double.NaN)}
    val movingAverage = mongoObject.getAs[Double](MOVINGAVG) orElse{Option(Double.NaN)}
    val extDivDate = mongoObject.getAs[String](EXDIVDATE) orElse{Option("N/A")}
    val peg = mongoObject.getAs[Double](PEG) orElse{mongoFail(PEG)}
    val shortRatio = mongoObject.getAs[Double](SHORTRATIO) orElse{Option(Double.NaN)}
    val marketPrice = mongoObject.getAs[String](MARKETCAP) 
  
    SharePrice(ticker.get, price.get, asOfDate.get, 
                currentEps.get, forwardEps.get, movingAverage.get,
                extDivDate.get, peg.get, shortRatio.get ,marketPrice.get)
    
  }
  
  def convertToMongoObject(sharePrice:SharePrice) = {
    val builder = MongoDBObject.newBuilder
    builder += ID -> null
    builder += TICKER -> sharePrice.ticker
    builder += ASOFDATE -> sharePrice.asOfDate
    builder += PRICE -> sharePrice.price
    builder += CURRENTEPS ->  sharePrice.currentEps 
    builder += FORWARDEPS -> sharePrice.forwardEps
    builder += MOVINGAVG -> sharePrice.movingAverage
    builder += EXDIVDATE -> sharePrice.exDivDate
    builder += PEG -> sharePrice.peg
    builder += SHORTRATIO -> sharePrice.shortRatio
    builder += CREATED_TIME -> new java.util.Date()
    builder += MARKETCAP -> sharePrice.marketCap
    builder.result()

    
  }
  
  
}