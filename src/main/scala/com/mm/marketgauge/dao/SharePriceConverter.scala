package com.mm.marketgauge.dao

import com.mongodb.casbah.Imports._
import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgauge.entities.SharePriceProperties._
import scala.util.Try
import scala.util.Success
import scala.util.Failure

object SharePriceConverter {

  private def mongoFail(field:String) = throw new Exception(s"Unable to access object for:$field")
  private val formatter = new java.text.SimpleDateFormat("MM/dd/yyyy")
  
  
  def convertToMongoObject(sharePrice: SharePrice): DBObject = {
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
    builder += CREATED_TIME -> getCreatedTime(sharePrice.asOfDate, sharePrice.ticker)
    builder += MARKETCAP -> sharePrice.marketCap
    builder.result()
  }

  def convertFromMongoObject(db: DBObject): SharePrice = {
    val id = db.getAs[ObjectId](ID) orElse {mongoFail(ID)}
    val ticker = db.getAs[String](TICKER) orElse{mongoFail(TICKER)}
    val asOfDate = db.getAs[String](ASOFDATE) orElse{mongoFail(ASOFDATE)}
    val price = db.getAs[Double](PRICE) orElse{mongoFail(PRICE)}
    val currentEps = db.getAs[Double](CURRENTEPS) orElse{mongoFail(CURRENTEPS)}
    val forwardEps = db.getAs[Double](FORWARDEPS) orElse{mongoFail(FORWARDEPS)}
    val movingAverage = db.getAs[Double](MOVINGAVG) orElse{mongoFail(MOVINGAVG)}
    val extDivDate = db.getAs[String](EXDIVDATE) orElse{mongoFail(EXDIVDATE)}
    val peg = db.getAs[Double](PEG) orElse{mongoFail(PEG)}
    val shortRatio = db.getAs[Double](SHORTRATIO) orElse{mongoFail(SHORTRATIO)}
    val marketPrice = db.getAs[String](MARKETCAP) 
  
    SharePrice(ticker.get, price.get, asOfDate.get, 
                currentEps.get, forwardEps.get, movingAverage.get,
                extDivDate.get, peg.get, shortRatio.get ,marketPrice.get)
    
  }
  
  private def getCreatedTime(dateTimeStr:String, ticker:String):java.util.Date = {
    Try(formatter.parse(dateTimeStr)) match {
      case Success(dTime) => dTime
      case Failure(ex)  => println(s"FAliued to extract date for:$ticker");new java.util.Date()
    }
  }
    
    
}
  
  


