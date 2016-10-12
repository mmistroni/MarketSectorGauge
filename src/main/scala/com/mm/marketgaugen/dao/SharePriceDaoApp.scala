package com.mm.marketgaugen.dao
import com.mm.marketgauge.entities.SharePrice
import com.typesafe.config._
object SharePriceDaoApp extends App {
  println("Creating share....")
  val conf = ConfigFactory.load()
  println("---------------------" + conf.getString("db.uri"))
  
  val ticker = "MyTicker"
  val share = new SharePrice(null, ticker, -1.0, "20160911",
                 -2.0, -3.0,
                 -4.0, "N.A",
                 Double.NaN, Double.NaN)

  println("Inserting...")
  val sharePriceDao = new SharePriceDao {
    val database = Database.getDatabase(conf.getString("db.username"), 
                                          conf.getString("db.password"),
                                          conf.getString("db.uri"),
                                          conf.getString("db.name"))
  }
  sharePriceDao.insert(share)
  
  println("Now retrieving...")
  
  val res:SharePrice = sharePriceDao.findByTicker(ticker).get
 
  println(s"Ticker=${res.ticker}|Price:${res.price}|Eps;${res.currentEps}")
  
  
  
}