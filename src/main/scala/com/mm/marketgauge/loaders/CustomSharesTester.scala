package com.mm.marketgauge.loaders

import com.mm.marketgauge.service.SharePriceServiceComponent
import com.mm.marketgauge.persistence.PersistenceServiceComponent
import com.typesafe.config.ConfigFactory

private[loaders] trait CustomSharesTester extends DataLoader with com.mm.marketgauge.util.LogHelper{
  self:SharePriceServiceComponent with PersistenceServiceComponent =>
  
  
  
  val conf = ConfigFactory.load()
  logger.info("Starting shares tester.....")
  
  logger.info("Sector Dao Loaded.....")
  
  logger.info(s"Args are:" + conf.getString("custom.shares"))
  
  val shares = conf.getString("custom.shares")
  logger.info(s"Loading shares:$shares")
  logger.info("---------------------" + conf.getString("db.uri"))
  
  def load = {
  
    for (ticker <- shares.split(",")) {
      logger.info(s"Fetching data for:$ticker")
      logger.info(s"Getting:${sharePriceService.downloadSharePrice(ticker)}")
    }
    shares.split(",").size
  }
   
  
  
}