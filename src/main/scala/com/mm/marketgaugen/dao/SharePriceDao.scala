package com.mm.marketgaugen.dao

import com.mm.marketgauge.entities.SharePrice
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import com.mm.marketgauge.converters.SharePriceConverter
import com.mongodb.casbah.Imports._

/**
 * User: talg
 */

object SharePriceDao {
  /**
   * Mongo URI string [[http://docs.mongodb.org/manual/reference/connection-string/]]
   */
  private val uri = """mongodb://localhost:27017/"""
  val db = MongoClient(MongoClientURI(uri))( """test""")
  val collection = db("share_prices")

  def insert(share: SharePrice) = {
    collection.insert(SharePriceConverter.convertToMongoObject(share))
  }

  def findByTicker(ticker :String) = {
    import com.mm.marketgauge.entities.SharePriceProperties.TICKER
    val query = MongoDBObject(TICKER -> ticker)
    convertFromMongo(collection.findOne(query))
  }

  def findByTickerAndAsOfDate(ticker :String, asOfDate:java.util.Date) = {
    import com.mm.marketgauge.entities.SharePriceProperties.{TICKER, ASOFDATE}
    val query = MongoDBObject(TICKER -> ticker,
                              ASOFDATE -> asOfDate)
    convertFromMongo(collection.findOne(query))
  }
  
  
  
  private def convertFromMongo(dbObj : Option[DBObject]) : Option[SharePrice]= {
    dbObj match {
      case Some(x) => Some(SharePriceConverter.convertFromMongoObject(x))
      case None => None
    }
  }
}