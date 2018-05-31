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
import org.json4s._
import org.json4s.native.JsonMethods._

@RunWith(classOf[JUnitRunner])
class JsonSharePriceServiceSpec extends FreeSpec with Matchers {
 
  val mockDownloader = Mockito.mock(classOf[DataDownloader])
  
  trait MockDataDownloaderComponent extends DataDownloaderComponent {
    override val dataDownloader = mockDownloader
  }
  
  val mockSharePriceDao = Mockito.mock(classOf[SharePriceDao])
  
  val mockSharePricerServiceComponent = new JsonSharePriceServiceComponent with MockDataDownloaderComponent 
  
  val mockSharePriceService = mockSharePricerServiceComponent.sharePriceService
  
  "The SharePriceService" - {
    "when calling downloadSharePrice it should call dataDownloader.downloadJson with sharePrice URL" - {
      "should return a SharePrice" in {
        
        val ticker = "AAPL"
        val jsonString = """{"symbol":"AAPL","companyName":"Apple Inc.","primaryExchange":"Nasdaq Global Select","sector":"Technology","calculationPrice":"close","open":187.2,"openTime":1527773400657,"close":186.87,"closeTime":1527796800500,"high":188.23,"low":186.14,"latestPrice":186.87,"latestSource":"Close","latestTime":"May 31, 2018","latestUpdate":1527796800500,"latestVolume":27326387,"iexRealtimePrice":186.92,"iexRealtimeSize":100,"iexLastUpdated":1527796784938,"delayedPrice":186.87,"delayedPriceTime":1527796800500,"extendedPrice":186.88,"extendedChange":0.01,"extendedChangePercent":0.00005,"extendedPriceTime":1527800290013,"previousClose":187.5,"change":-0.63,"changePercent":-0.00336,"iexMarketPercent":0.01929,"iexVolume":527126,"avgTotalVolume":30631325,"iexBidPrice":0,"iexBidSize":0,"iexAskPrice":0,"iexAskSize":0,"marketCap":918491838060,"peRatio":19.21,"week52High":190.37,"week52Low":142.2,"ytdChange":0.09712319669517237}"""
        val jsonValue = parse(jsonString)
        val sharePriceUrl = mockSharePriceService.sharePriceUrl.replace("<ticker>", ticker);
                        
        Mockito.when(mockDownloader.downloadJson(sharePriceUrl)).thenReturn(jsonValue)
        val sharePriceResult = mockSharePriceService.downloadSharePrice(ticker).get
        sharePriceResult.price should be("186.87".toDouble)
        sharePriceResult.ticker should be("AAPL")
        sharePriceResult.asOfDate should be ("May 31, 2018")
        sharePriceResult.marketCap should be ("918491838060")
        Mockito.verify(mockDownloader).downloadJson(sharePriceUrl)
        
      }
    }
  }
  
  
  
  
}