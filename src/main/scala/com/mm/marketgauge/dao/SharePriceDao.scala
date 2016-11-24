package com.mm.marketgauge.dao

import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgauge.entities.SharePriceProperties
import com.mongodb.casbah.Imports.DBObject
import com.mongodb.casbah.Imports.MongoDBObject

/**
 * User: talg
 */

trait SharePriceDao extends BaseDao with com.mm.marketgauge.util.LogHelper {
  /**
   * Mongo URI string [[http://docs.mongodb.org/manual/reference/connection-string/]]
   */
  lazy val collection = database.client("share_prices")

  def insert(shares: SharePrice*) = {
    val builder = collection.initializeOrderedBulkOperation
    shares.foreach(s => builder.insert(SharePriceConverter.convertToMongoObject(s)))
    builder.execute().insertedCount
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