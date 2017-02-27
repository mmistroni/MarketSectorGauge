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
  val mockSectorService = Mockito.mock(classOf[SectorService])
  val mockSharePriceDao = Mockito.mock(classOf[SharePriceDao])
  val mockSharePriceService = 
    new SharePriceService {
            val dataDownloader = mockDownloader
            val sectorService = mockSectorService
            val sharePriceDao = mockSharePriceDao
                      }
  "The SharePriceService" - {
    "when calling downloadSharePrice it should call dataDownloader.downloadCSV with sharePrice URL" - {
      "should return a SharePrice" in {
        
        val ticker = "GE";
        
        val yahooData = List(ticker, "1.0", "12/13/2016", "3.0", "4.0", "5.0", "", "6.0", "7.0", "20B")
        val sharePriceUrl = mockSharePriceService.sharePriceUrl.replace("<ticker>", ticker);
                        
        Mockito.when(mockDownloader.downloadCSV(sharePriceUrl)).thenReturn(List(yahooData))
        val sharePriceResult = mockSharePriceService.downloadSharePrice(ticker).get
        sharePriceResult.price should be(yahooData(1).toDouble)
        sharePriceResult.ticker should be(ticker)
        sharePriceResult.currentEps should be(yahooData(3).toDouble)
        sharePriceResult.asOfDate should be (yahooData(2))
        sharePriceResult.marketCap should be (yahooData(9))
        Mockito.verify(mockDownloader).downloadCSV(sharePriceUrl)
        
      }
    }
  }
  
  "The SharePriceService" - {
    "when calling persistSharePrices should call sharePriceDao.insert" - {
      "and should return an integer representing number of inserts" in {
        
        val ticker = "GE";
        
        val testSharePrice = new SharePrice(ticker, 1.0, 
                    "12/13/2016",
                     Double.NaN, Double.NaN,
                     Double.NaN, "i dont know",
                     Double.NaN, Double.NaN,
                     "10B")
           
        val prices = List(testSharePrice)
        val numInsert = prices.size
        Mockito.when(mockSharePriceDao.insert(prices: _*)).thenReturn(numInsert)
        val sharePriceResult = mockSharePriceService.persistSharePrices(prices)
        sharePriceResult should be (numInsert)
        Mockito.verify(mockSharePriceDao).insert(prices: _*)
        
      }
    }
  }
  
  
  
  
}