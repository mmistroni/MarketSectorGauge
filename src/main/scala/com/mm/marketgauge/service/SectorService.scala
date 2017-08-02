package com.mm.marketgauge.service

import scala.util.{Try, Success, Failure}
import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgauge.dao.SectorDao
import com.mm.marketgauge.util.LogHelper

trait SectorServiceComponent {
  this: DataDownloaderComponent =>

  val dataService: SectorService = new SectorService {}

  /**
   * Service for handling Sector data
   * REFACTOR this.  extractXXX is common to all sector..perhaps extract out?
   * Also see if code can be more function-oriented
   */
  trait SectorService {
    private[service] val allSectorsUrl = "https://biz.yahoo.com/ic/ind_index_alpha.html"
    private[service] val sectorDataUrl = "https://biz.yahoo.com/ic/<sectorId>.html"

    def downloadAllSectorsIds: Seq[Int] = {
      _extractSectorIds(dataDownloader.downloadFromURL(allSectorsUrl))
    }

    def downloadSectorData(sectorId: Int): Option[Sector] = { // Replace with Try, to avoid java-esque Try/catch
        logger.info(s"Downloading data for $sectorId")
        val sectorData = Try(_extractSectorData(
            dataDownloader.downloadFromURL(sectorDataUrl.replace("<sectorId>", sectorId.toString)),
                                            sectorId))
        sectorData.toOption
      
    }

    def _extractSectorIds(lines: Iterator[String]): Seq[Int] = {
      // hardcoded here to fetch data from yahoo sectorss
      val l = lines.filter(line => line.indexOf("https://biz.yahoo.com/ic/") > 0 && line.indexOf(".html") > 0).
        map(line => line.substring(line.indexOf("https://biz.yahoo.com/ic") + 25, line.indexOf(".html"))).
        filter(line => line.forall(_.isDigit)).map(_.toInt).toList
      logger.info("Got:" + l.mkString(":"))
      l
    }

    def _extractSectorData(lines: Iterator[String], sectorId: Int): Sector = {
        logger.info(s"Extracting data for sector:$sectorId")
        val data = lines.toList.filter(line => line.indexOf("^YHO") > 0 && line.indexOf("]") > 0 && line.size > 0)
        val mapped = data.map(line => line.substring(line.indexOf("[") + 1, line.indexOf("]"))).toList
        val dataArray = mapped.head.split('(')
        Sector(dataArray(0).trim, dataArray(1).substring(0, dataArray(1).indexOf(")")).trim,
          sectorId, "yhoo")
      
    }

  }
}