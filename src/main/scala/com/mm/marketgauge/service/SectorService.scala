package com.mm.marketgauge.service


import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgaugen.dao.SectorDao

/**
 * Service for handling Sector data
 */
trait SectorService {
  private[service] val allSectorsUrl = "https://biz.yahoo.com/ic/ind_index_alpha.html"
  private[service] val sectorDataUrl = "https://biz.yahoo.com/ic/<sectorId>.html"
  private[service] val dataDownloader:DataDownloader 
  private[service] val sectorDao:SectorDao
  
  def downloadAllSectorsIds:Seq[Int] = {
    _extractSectorIds(dataDownloader.downloadFromURL(allSectorsUrl))
  }
  
  def downloadSectorData(sectorId:Int):Sector = {
    _extractSectorData(dataDownloader.downloadFromURL(sectorDataUrl.replace("<sectorId>", sectorId.toString)),
                                                      sectorId)
  }
  
  def getAllSectors:Seq[Sector] = {null}
  
  def persistSectors(sectors:Seq[Sector]):Unit = {
    sectors.foreach(s => sectorDao.insertSector(s)) 
  }
 
  def _extractSectorIds(lines:Iterator[String]):Seq[Int] = {
    lines.filter(line => line.indexOf("https://biz.yahoo.com/ic/") > 0 && line.indexOf(".html") > 0).
            map(line => line.substring(line.indexOf("https://biz.yahoo.com/ic")+25, line.indexOf(".html"))).
            filter(line => line.forall(_.isDigit)).map(_.toInt).toList
  }
  
  def _extractSectorData(lines:Iterator[String], sectorId:Int):Sector = {
    val data = lines.filter(line => line.indexOf("^YHO") > 0 && line.indexOf("]")> 0)
    val mapped = data.map(line=>line.substring(line.indexOf("[")+1, line.indexOf("]"))).toList
    val dataArray =  mapped.head.split('(')
    Sector(null, dataArray(0).trim, dataArray(1).substring(0, dataArray(1).indexOf(")")).trim,
           sectorId, "yhoo")
     
  }
  
}