package com.mm.marketgaugen.dao

import com.mm.marketgauge.entities.SharePrice
import com.mongodb.casbah.{MongoClient, MongoClientURI, WriteConcern}
import com.mm.marketgauge.converters.SharePriceConverter
import com.mongodb.casbah.Imports._

/**
 * User: talg
 */

trait SharePriceDao {
  /**
   * Mongo URI string [[http://docs.mongodb.org/manual/reference/connection-string/]]
   */
  val uri : String // = """mongodb://localhost:27017/"""
  lazy val db = MongoClient(MongoClientURI(uri))( """test""")
  lazy val collection = db("share_prices")

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