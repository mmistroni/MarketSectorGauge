 package com.mm.marketgauge.integration;

 
import com.mm.marketgauge.service.JsonDataDownloader 
 
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.json4s._
import org.json4s.native.JsonMethods._



@RunWith(classOf[JUnitRunner])
class DataDownloaderIntegSpec extends FreeSpec with Matchers {
 
  "The DataDownloader" - {
    "when calling downloadJson with a String representing a ticker" - {
      "should return a JValue " in {
        
        val testTicker = "AAPL"
        val dataDownloader = new JsonDataDownloader{}  
        val tickerUrl = "https://api.iextrading.com/1.0/stock/<ticker>/quote".replace("<ticker>", testTicker)
        val result = dataDownloader.downloadJson(tickerUrl)

        assert(result.isInstanceOf[JValue])
        
        
      }
    }
  }
  
  
  
  
}