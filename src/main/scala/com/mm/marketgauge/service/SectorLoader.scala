package com.mm.marketgauge.service
import com.mm.marketgauge.dao.{SectorDao, Database}
import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.util.LogHelper
import com.typesafe.config._
import amazon.util.AWSClientFactory


object SectorLoader extends App with LogHelper {

  val downloader = new DataDownloader {}
  val conf = ConfigFactory.load()
  val topic = conf.getString("aws.topic")
  val notifier = AWSClientFactory.snsClient(conf.getString("aws.accessKey"),
                                            conf.getString("aws.secretKey"))
      
  val sectorService = new SectorService {
    val dataDownloader = downloader
    logger.info("db.util"+ conf.getString("db.uri"))
    logger.info("db.name" + conf.getString("db.name"))
    
    
    val sectorDao = new SectorDao {

      val database = Database.getDatabase(conf.getString("db.username"),
        conf.getString("db.password"),
        conf.getString("db.uri"),
        conf.getString("db.name"))

    }

  }

  
  def load = {
    val allSectorsId = sectorService.downloadAllSectorsIds
    logger.info("All sector downoaded. now mapping them...")
    // Option idiom, same as in SharePriceLoader
    val allSectors = allSectorsId.flatMap(sectorService.downloadSectorData(_)) //, this will take care of the nulls
    logger.info("Got all sectors...")
    val res = sectorService.sectorDao.insertBulk(allSectors)
    
    logger.info(s"Inserted $res rows..Now notifying...with topic:$topic")
    notifier.publishToTopic(topic, "MarketSectorGauge. Sector upload", s"$res sectors were uploaded")
     
    
  }

  logger.info(".... Loading...")
  load

}