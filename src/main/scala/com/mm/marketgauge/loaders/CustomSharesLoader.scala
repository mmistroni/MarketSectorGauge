package com.mm.marketgauge.loaders

import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.persistence.PersistenceServiceComponent
import com.typesafe.config.ConfigFactory


private [loaders] trait CustomSharesLoader extends DataLoader with com.mm.marketgauge.util.LogHelper{
  self:PersistenceServiceComponent =>
  
  val conf = ConfigFactory.load()
  
  def load = {
    logger.info("Starting shares loader.....")
    val shares = conf.getString("custom.shares")
    logger.info(s"Loading shares:$shares")
    val sectors = shares.split(",").map(ticker => Sector( ticker, ticker, -1 * ticker.hashCode(), "manual"))
    logger.info("Inserting:" + sectors.mkString(":"))
    persistenceService.storeSectors(sectors)
    
  }
  
  
}