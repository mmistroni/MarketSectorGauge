package com.mm.marketgauge.service

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._
import com.mm.marketgauge.entities.Sector
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class SectorServiceDownloaderSpec extends FreeSpec with Matchers {
 
  val mockDownloader = Mockito.mock(classOf[DataDownloader])
  val mockSectorService = 
    new SectorService {
            val dataDownloader = mockDownloader
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
        val expectedSector = Sector("Oil & Gas Equipment & Services", 
                                    "^YHOh708", 124, "yhoo")
        
        Mockito.when(mockDownloader.downloadFromURL(expectedUrl)).thenReturn(yahooData.iterator)
        val sector = mockSectorService.downloadSectorData(sectorId)
        sector shouldEqual(expectedSector)
        Mockito.verify(mockDownloader).downloadFromURL(expectedUrl)
        
      }
    }
  }
  
  
}