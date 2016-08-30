package com.mm.marketgauge.service
import com.mm.marketgaugen.dao.SectorDao
import com.mm.marketgauge.entities.Sector
object SectorLoaderApp extends App{
  
  val sectorService = new SectorService {
            val dataDownloader = new DataDownloader{}
            val sectorDao = new SectorDao{}
                      
  }
  
  
  val allSectorsId = sectorService.downloadAllSectorsIds
  println("Got:" + allSectorsId.size)
  val allSectors = allSectorsId.map(sectorService.downloadSectorData(_)).take(25)
  println("Got all sectors...")
  
  val res = sectorService.sectorDao.insertBulk(allSectors)
  println(s"Inserted $res rows...")
  
}