package com.mm.marketgauge.loaders

import com.mm.marketgauge.persistence.PersistenceServiceComponent
import com.mm.marketgauge.persistence.mongo._
import com.mm.marketgauge.service.SectorServiceComponent
import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.util.LogHelper
import com.typesafe.config.ConfigFactory

import amazon.util.AWSClientFactory

/**
 * Refactor. Too much code and too much stuff defined here
 * This is an ETL:
 * 1. E - load data from Db
 * 2. T - download data from Web
 * 3. L - store persisted data.this can be extracted out. And the load function
 *    does too much
 */
private[loaders] trait SectorLoader extends DataLoader with LogHelper { 
  self:SectorServiceComponent with PersistenceServiceComponent =>
  
  
  private[loaders] def fetchSectorIds :Seq[Int] = {
    dataService.downloadAllSectorsIds
  }
    
  private[loaders] def downloadSectors(allSectorsIds:Seq[Int]):Seq[Sector] = {
    logger.info("All sector downoaded. now mapping them...")
    allSectorsIds.flatMap(sectorId => dataService.downloadSectorData(sectorId)) //, this will take care of the nulls
  } 
  
  
  private[loaders] def fetchSectors :Seq[Sector] = {
    val allSectorsIds = fetchSectorIds
    logger.info("All sector downoaded. now mapping them...")
    downloadSectors(allSectorsIds)
  }
  
  
  
  def load:Int = {
    logger.info("Got all sectors...")
    val allSectors = fetchSectors
    val res = sectorRepository.insert(allSectors)
    notify("MarketSectorGauge. Sector upload", s"$res sectors were uploaded")
    res 
    
  }
  
  
}