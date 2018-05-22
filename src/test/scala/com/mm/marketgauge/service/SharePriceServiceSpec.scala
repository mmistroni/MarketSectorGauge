package com.mm.marketgauge.service

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgauge.dao.SharePriceDao


@RunWith(classOf[JUnitRunner])
class SharePriceServiceSpec extends FreeSpec with Matchers {
 
  val mockDownloader = Mockito.mock(classOf[DataDownloader])
  
  trait MockDataDownloaderComponent extends DataDownloaderComponent {
    override val dataDownloader = mockDownloader
  }
  
  val mockSharePriceDao = Mockito.mock(classOf[SharePriceDao])
  
  val mockSharePricerServiceComponent = new SharePriceServiceComponent with MockDataDownloaderComponent 
  
  val mockSharePriceService = mockSharePricerServiceComponent.sharePriceService
  
  "The SharePriceService" - {
    "when calling downloadSharePrice it should call dataDownloader.downloadCSV with sharePrice URL" - {
      "should return a SharePrice" in {
        
        val ticker = "GE";
        
        val yahooData = List("12/13/2016", "1.0","2.0" , "3.0", "4.0", "5.0", "", "6.0", "7.0", "20B")
        val headers = List.fill(9)("test")
        val sharePriceUrl = mockSharePriceService.sharePriceUrl.replace("<ticker>", ticker);
                        
        Mockito.when(mockDownloader.downloadCSV(sharePriceUrl)).thenReturn(List(headers,yahooData))
        val sharePriceResult = mockSharePriceService.downloadSharePrice(ticker).get
        sharePriceResult.price should be(yahooData(4).toDouble)
        sharePriceResult.ticker should be(ticker)
        sharePriceResult.currentEps should be(yahooData(3).toDouble)
        sharePriceResult.asOfDate should be (yahooData(0))
        sharePriceResult.marketCap should be ("N/A")
        Mockito.verify(mockDownloader).downloadCSV(sharePriceUrl)
        
      }
    }
  }
  
  
  
  
}