package com.mm.marketgauge.service
import com.mm.marketgaugen.dao.SectorDao
import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.util.LogHelper
import com.typesafe.config._

object SectorLoader extends App with LogHelper {

  val downloader = new DataDownloader {}
  val conf = ConfigFactory.load()
  
  
  
  val sectorService = new SectorService {
    val dataDownloader = downloader
    val sectorDao = new SectorDao {
      
       val uri = conf.getString("db.uri")
      
    }

  }

  def load = {
    val allSectorsId = sectorService.downloadAllSectorsIds
    logger.info("All sector downoaded. now mapping them...")
    val allSectors = allSectorsId.map(sectorService.downloadSectorData(_)).filter(sec => sec != null)
    logger.info("Got all sectors...")
    val res = sectorService.sectorDao.insertBulk(allSectors)
    logger.info(s"Inserted $res rows...")

  }

  
  logger.info(".... Loading...")
  load
  
  
  
}