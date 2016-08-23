package com.mm.marketgauge.service

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgauge.entities.SharePrice


@RunWith(classOf[JUnitRunner])
class SharePriceServiceSpec extends FreeSpec with Matchers {
 
  val mockDownloader = Mockito.mock(classOf[DataDownloader])
  val mockSectorService = Mockito.mock(classOf[SectorService])
  val mockSharePriceService = 
    new SharePriceService {
            val dataDownloader = mockDownloader
            val sectorService = mockSectorService
                      }
  "The SharePriceService" - {
    "when calling downloadSharePrice it should call dataDownloader.downloadCSV with sharePrice URL" - {
      "should return a SharePrice" in {
        
        val ticker = "GE";
        val simpleDateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy")
        
        val yahooData = List(ticker, "1.0", "12/13/2016", "N/A", "N/A", "N/A", "", "N/A", "N/A")
        val sharePriceUrl = mockSharePriceService.sharePriceUrl.replace("<ticker>", ticker);
                        
        Mockito.when(mockDownloader.downloadCSV(sharePriceUrl)).thenReturn(List(yahooData))
        val sharePriceResult = mockSharePriceService.downloadSharePrice(ticker)
        sharePriceResult.price should be(yahooData(1).toDouble)
        sharePriceResult.ticker should be(ticker)
        sharePriceResult.currentEps should be(Double.NaN)
        simpleDateFormat.format(sharePriceResult.asOfDate) should be (yahooData(2))
        Mockito.verify(mockDownloader).downloadCSV(sharePriceUrl)
        
      }
    }
  }
  
  
}