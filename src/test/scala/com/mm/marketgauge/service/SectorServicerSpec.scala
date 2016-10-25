package com.mm.marketgauge.service

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._
import com.mm.marketgauge.entities.Sector
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgaugen.dao.SectorDao


@RunWith(classOf[JUnitRunner])
class SectorServiceDownloaderSpec extends FreeSpec with Matchers {
 
  val mockDownloader = Mockito.mock(classOf[DataDownloader])
  val mockSectorDao = Mockito.mock(classOf[SectorDao])
  val mockSectorService = 
    new SectorService {
            val dataDownloader = mockDownloader
            val sectorDao = mockSectorDao
                      }
  "The SectorService" - {
    "when calling getAllSectorsIds it should call dataDownloader.downloader with allSectorsUrl URL" - {
      "should return a sequence of Sector Ids" in {
        
        val yahooData = List("<a href=\"https://biz.yahoo.com/ic/1.html\">Test1</a>",
                             "<a href=\"https://biz.yahoo.com/ic/2.html\">Test2</a>")
                        
        Mockito.when(mockDownloader.downloadFromURL(mockSectorService.allSectorsUrl)).thenReturn(yahooData.iterator)
        val expectedSectorIds = List(1,2)        
        val sectorIds = mockSectorService.downloadAllSectorsIds
        sectorIds shouldEqual(expectedSectorIds)
        Mockito.verify(mockDownloader).downloadFromURL(mockSectorService.allSectorsUrl)
        
      }
    }
  }
  
  "The SectorService" - {
    "when calling downloadSectorData it should call dataDownloader.downloader with sectorDataUrl URL" - {
      "should return a Sector" in {
        
        val sectorId = 124
        val expectedUrl = mockSectorService.sectorDataUrl.replace("<sectorId>", sectorId.toString)
        val yahooData = List("[Oil & Gas Equipment & Services (^YHOh708)]</a></td></tr></table></td></tr></table><table><tr><td")
        val expectedSector = Sector(null, "Oil & Gas Equipment & Services", 
                                    "^YHOh708", 124, "yhoo")
        
        Mockito.when(mockDownloader.downloadFromURL(expectedUrl)).thenReturn(yahooData.iterator)
        val sector = mockSectorService.downloadSectorData(sectorId).get
        sector shouldEqual(expectedSector)
        
      }
    }
  }
  
  "The SectorService" - {
    "when calling downloadSectorData it should call dataDownloader.downloader with sectorDataUrl URL" - {
      "should return None if dataDownloader raises exception" in {
        
        val sectorId = 124
        val expectedUrl = mockSectorService.sectorDataUrl.replace("<sectorId>", sectorId.toString)
        val yahooData = List("[Oil & Gas Equipment & Services (^YHOh708)]</a></td></tr></table></td></tr></table><table><tr><td")
        val expectedSector = Sector(null, "Oil & Gas Equipment & Services", 
                                    "^YHOh708", 124, "yhoo")
        
        Mockito.when(mockDownloader.downloadFromURL(expectedUrl)).thenThrow(new java.lang.IllegalArgumentException("Data not found"))
        val sector = mockSectorService.downloadSectorData(sectorId)
        sector shouldEqual(None)
        
      }
    }
  }
  
  "The SectorService" - {
    "when calling persistSectors it should call  sectorDao" - {
      "should return count" in {
        
        val expectedSector = Sector(null, "Oil & Gas Equipment & Services", 
                                    "^YHOh708", 124, "yhoo")
        
        val sectors = List(expectedSector)
                                    
        mockSectorService.persistSectors(sectors)
        Mockito.verify(mockSectorDao, Mockito.times(1)).insertSector(expectedSector)
          
      }
    }
  }
  
  "The SectorService" - {
    "when calling getAllSectors should call  sectorDao" - {
      "and return all sectors" in {
        
        val expectedSector = Sector(null, "Oil & Gas Equipment & Services", 
                                    "^YHOh708", 124, "yhoo")
        
        val sectors = List(expectedSector)
                                    
        Mockito.when(mockSectorDao.findAll).thenReturn(sectors.iterator)
        val res = mockSectorService.getAllSectors
        Mockito.verify(mockSectorDao).findAll
        res should be(sectors)
          
      }
    }
  }
  
  
  
  
  
  
  
}