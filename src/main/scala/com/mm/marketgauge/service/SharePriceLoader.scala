package com.mm.marketgauge.service

import com.mm.marketgauge.dao.{SectorDao, SharePriceDao, Database}
import com.mm.marketgauge.entities.{Sector, SharePrice}
import com.mm.marketgauge.util.LogHelper
import com.typesafe.config._



object SharePriceLoader extends App with LogHelper{
  
  val downloader = new DataDownloader {}
  val conf = ConfigFactory.load()
    
  val sectorService = new SectorService {
    val dataDownloader = downloader
    val sectorDao = new SectorDao {
      val database = Database.getDatabase(conf.getString("db.username"), 
                                          conf.getString("db.password"),
                                          conf.getString("db.uri"),
                                          conf.getString("db.name"))
      
    }

  }
  
  val sharePriceService = new SharePriceService {
    val dataDownloader = downloader
    val sharePriceDao = new SharePriceDao {
      val database = Database.getDatabase(conf.getString("db.username"), 
                                          conf.getString("db.password"),
                                          conf.getString("db.uri"),
                                          conf.getString("db.name"))
      
    }
  }


  def loadSectors = {
    logger.info("Loading all sectors.....")
    sectorService.getAllSectors
  }

  def loadPrices(sectors:Seq[Sector]) = {
    logger.info(s"Loading prices for ${sectors.size} sectors")
    sectors.flatMap(sector => sharePriceService.downloadSharePrice(sector.ticker))
  }
  
  def persistPrices(prices:Seq[SharePrice]) = {
    val valids = prices.filter(_ != null)
    logger.info(s"Persisting ${valids.size} prices...")
    val numRows = sharePriceService.persistSharePrices(valids)
    logger.info(s"From ${prices.size} we  persisted $numRows prices")
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


