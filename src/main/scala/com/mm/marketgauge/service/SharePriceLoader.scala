package com.mm.marketgauge.service

import com.mm.marketgauge.util.LogHelper
import com.mm.marketgaugen.dao.{SectorDao, SharePriceDao}
import com.mm.marketgauge.entities.{Sector, SharePrice}
import com.mm.marketgauge.util.LogHelper



object SharePriceLoader extends App with LogHelper{
  
  val downloader = new DataDownloader {}
    
  val sectorService = new SectorService {
    val dataDownloader = downloader
    val sectorDao = new SectorDao {}

  }
  
  val sharePriceService = new SharePriceService {
    val dataDownloader = downloader
    val sharePriceDao = new SharePriceDao {}
  }


  def loadSectors = {
    logger.info("Loading all sectors.....")
    sectorService.getAllSectors
  }

  def loadPrices(sectors:Seq[Sector]) = {
    logger.info(s"Loading prices for ${sectors.size} sectors")
    sectors.map(sector => sharePriceService.downloadSharePrice(sector.ticker))
  }
  
  def persistPrices(prices:Seq[SharePrice]) = {
    val valids = prices.filter(_ != null)
    logger.info(s"Persisting ${valids.size} prices...")
    sharePriceService.persistSharePrices(valids)
  }
  
  def load = {
    val sectors = loadSectors
    logger.info(s"Loaded ${sectors.size} sectors....")
    val prices = loadPrices(sectors)
    persistPrices(prices)
  }
  
  logger.info("Starting load...")
  load
  
}


