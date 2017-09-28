package com.mm.marketgauge.loaders

import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgauge.persistence.PersistenceServiceComponent
import com.mm.marketgauge.service.SharePriceServiceComponent
import com.mm.marketgauge.util.LogHelper
import com.typesafe.config.ConfigFactory

private [loaders] trait SharePriceLoader extends DataLoader with LogHelper{
  self:SharePriceServiceComponent with PersistenceServiceComponent =>
     
  
  def loadSectors = {
    sectorRepository.getAll
  }

  def loadPrices(sectors:Seq[Sector]) = {
    logger.info(s"Loading prices for ${sectors.size} sectors")
    sectors.flatMap(sector => sharePriceService.downloadSharePrice(sector.ticker))
  }
  
  def persistPrices(prices:Seq[SharePrice]) = {
    logger.info(s"Persisting ${prices.size} prices...")
    sharePriceRepository.insert(prices)
  }
  
  def load = {
    val sectors = loadSectors
    logger.info(s"Loaded ${sectors.size} sectors....")
    val prices = loadPrices(sectors)
    persistPrices(prices)
  }
  
}


