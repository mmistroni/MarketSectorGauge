package com.mm.marketgaugen.dao
import com.mm.marketgauge.entities.SharePrice
object SharePriceDaoApp extends App {
  println("Creating share....")
  
  val ticker = "MyTicker"
  val share = new SharePrice(null, ticker, -1.0, new java.util.Date(),
                 -2.0, -3.0,
                 -4.0, "N.A",
                 Double.NaN, Double.NaN)

  println("Inserting...")
  SharePriceDao.insert(share)
  
  println("Now retrieving...")
  
  val res:SharePrice = SharePriceDao.findByTicker(ticker).get
 
  println(s"Ticker=${res.ticker}|Price:${res.price}|Eps;${res.currentEps}")
  
  
  
}