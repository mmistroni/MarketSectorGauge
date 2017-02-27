package com.mm.marketgauge.service
import com.mm.marketgauge.entities.Sector
import com.typesafe.config._
import com.mm.marketgauge.dao.Database
import com.mm.marketgauge.dao.SharePriceDao
object CustomSharesTester extends App with com.mm.marketgauge.util.LogHelper{
  val downloader = new DataDownloader {}
  
  val conf = ConfigFactory.load()
  
  logger.info("Starting shares tester.....")
  
  val sharePriceService = new SharePriceService {
    val dataDownloader = downloader
    val sharePriceDao = new SharePriceDao {
      val database = Database.getDatabase(conf.getString("db.username"), 
                                          conf.getString("db.password"),
                                          conf.getString("db.uri"),
                                          conf.getString("db.name"))
      
    }
  }
  
  logger.info("Sector Dao Loaded.....")
  
  logger.info(s"Args are:" + conf.getString("custom.shares"))
  
  val shares = conf.getString("custom.shares")
  logger.info(s"Loading shares:$shares")
  logger.info("---------------------" + conf.getString("db.uri"))
  
  
  
  for (ticker <- shares.split(",")) {
    logger.info(s"Fetching data for:$ticker")
    logger.info(s"Getting:${sharePriceService.downloadSharePrice(ticker)}")
    
    
  }
   
  
  
}