package com.mm.marketgauge.service

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._
import com.mm.marketgauge.entities.Sector
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgauge.dao.SectorDao


@RunWith(classOf[JUnitRunner])
class SectorServiceDownloaderSpec extends FreeSpec with Matchers {
 
  val mockDownloader = Mockito.mock(classOf[DataDownloader])
  val mockSectorDao = Mockito.mock(classOf[SectorDao])

  trait MockDataDownloaderComponent extends DataDownloaderComponent {
    override val dataDownloader = mockDownloader
  }
  
  
  val mockSectorServiceComponent = new SectorServiceComponent with MockDataDownloaderComponent 
  
  val sectorService = mockSectorServiceComponent.dataService
  
  
                      
  "The SectorService" - {
    "when calling getAllSectorsIds it should call dataDownloader.downloader with allSectorsUrl URL" - {
      "should return a sequence of Sector Ids" in {
        
        val yahooData = List("<a href=\"https://biz.yahoo.com/ic/1.html\">Test1</a>",
                             "<a href=\"https://biz.yahoo.com/ic/2.html\">Test2</a>")
        
        Mockito.when(mockDownloader.downloadFromURL(sectorService.allSectorsUrl)).thenReturn(yahooData.iterator)
        val expectedSectorIds = List(1,2)        
        val sectorIds = sectorService.downloadAllSectorsIds
        sectorIds shouldEqual(expectedSectorIds)
        Mockito.verify(mockDownloader).downloadFromURL(sectorService.allSectorsUrl)
        
      }
    }
  }
  
  "The SectorService" - {
    "when calling downloadSectorData it should call dataDownloader.downloader with sectorDataUrl URL" - {
      "should return a Sector" in {
        
        val sectorId = 124
        val expectedUrl = sectorService.sectorDataUrl.replace("<sectorId>", sectorId.toString)
        val yahooData = List("[Oil & Gas Equipment & Services (^YHOh708)]</a></td></tr></table></td></tr></table><table><tr><td")
        val expectedSector = Sector("Oil & Gas Equipment & Services", 
                                    "^YHOh708", 124, "yhoo")
        
        Mockito.when(mockDownloader.downloadFromURL(expectedUrl)).thenReturn(yahooData.iterator)
        val sector = sectorService.downloadSectorData(sectorId).get
        sector shouldEqual(expectedSector)
        
      }
    }
  }
  
  "The SectorService" - {
    "when calling downloadSectorData it should call dataDownloader.downloader with sectorDataUrl URL" - {
      "should return None if dataDownloader raises exception" in {
        
        val sectorId = 124
        val expectedUrl = sectorService.sectorDataUrl.replace("<sectorId>", sectorId.toString)
        val yahooData = List("[Oil & Gas Equipment & Services (^YHOh708)]</a></td></tr></table></td></tr></table><table><tr><td")
        val expectedSector = Sector( "Oil & Gas Equipment & Services", 
                                    "^YHOh708", 124, "yhoo")
        
        Mockito.when(mockDownloader.downloadFromURL(expectedUrl)).thenThrow(new java.lang.IllegalArgumentException("Data not found"))
        val sector = sectorService.downloadSectorData(sectorId)
        sector shouldEqual(None)
        
      }
    }
  }
  
}