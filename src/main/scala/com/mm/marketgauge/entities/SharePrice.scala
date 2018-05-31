package com.mm.marketgauge.entities
import org.bson.types.ObjectId

case class SharePrice(ticker:String, price:Double, asOfDate:String,
                 currentEps:Double, forwardEps:Double,
                 movingAverage:Double, exDivDate:String,
                 peg:Double, shortRatio:Double, marketCap:String)
                 
case class StockData(latestPrice: Double, marketCap: Long, open: Double,
                     previousClose: Double, companyName: String,
                     ytdChange: Double, latestVolume: Long, week52Low: Double,
                     week52High: Double, symbol: String, peRatio: String,
                     latestTime:String)

                 
object SharePriceProperties {
  val ID = "_id"
  val TICKER = "ticker"
  val ASOFDATE = "asofdate"
  val PRICE = "price"
  val CURRENTEPS = "currentEps"
  val FORWARDEPS = "forwardEps"
  val MOVINGAVG = "movingAvg"
  val EXDIVDATE = "exDivDate"
  val PEG = "peg"
  val SHORTRATIO = "shortRatio"
  val CREATED_TIME = "created_time"
  val MARKETCAP = "marketCap"
}                

