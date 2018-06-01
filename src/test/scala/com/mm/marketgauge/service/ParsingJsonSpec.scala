package com.mm.marketgauge.service

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._
import com.mm.marketgauge.entities.Sector
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scala.util.parsing.json._
import scala.io._
import org.json4s._
import org.json4s.native.JsonMethods._

case class StockData(latestPrice: Double, marketCap: Long, open: Double,
                     previousClose: Double, companyName: String,
                     ytdChange: Double, latestVolume: Long, week52Low: Double,
                     week52High: Double, symbol: String, peRatio: String)

@RunWith(classOf[JUnitRunner])
class ParsingJsonSpec extends FreeSpec with Matchers {

  private val baseUrl = "https://api.iextrading.com/1.0/stock/%s/quote"
  implicit val formats = DefaultFormats

  def parseWithScalaJson(jsonString: String): Unit = {
    val result = JSON.parseFull(jsonString)
    result match {
      // Matches if jsonStr is valid JSON and represents a Map of Strings to Any
      case Some(map: Map[String, Any]) => println(map)
      case None                        => println("Parsing failed")
      case other                       => println("Unknown data structure: " + other)
    }
  }

  def parseWithJson4s(jsonString: String): Unit = {
    val json = parse(jsonString)

    val stockData = json.extract[StockData]
    
  }

  def parseJson(jsonUrl: String): String = {
    Source.fromURL(jsonUrl).mkString
  }

  "The ParsingJsonService for aapl tock" - {
    "when parseJson with  an URL" - {
      "should return a result" in {

        
        val aaplUrl = baseUrl.format("AAPL")

        val jsonStr = Source.fromURL("https://api.iextrading.com/1.0/stock/%s/quote".format("AAPL")).mkString

        
        
        
        //parseWithScalaJson(jsonStr)

        println(" THE JSON STRING IS-------------------------------")

        println(jsonStr)
        println("-----------------")
        
        parseWithJson4s(jsonStr)

        //
        //https://stackoverflow.com/questions/4170949/how-to-parse-json-in-scala-using-standard-scala-classes
        
      }
    }
  }

  
  "The ParsingJsonService for sector stock" - {
    "when parseJson with  an URL" - {
      "should return a result" in {

        val sectors = Seq("IYC", // Consumer discretionary
          "IYK", // consumer staples
          "IYE", // energy
          "IYF", // financials
          "IYH", // healh care
          "IYJ", // Industrials
          "IYM", //materials 
          "IYW", // info tech
          "IYZ", //telecom
          "IDU", //utilities 
          "XLU")
          

        val mappedUrl = sectors.map(ticker => "https://api.iextrading.com/1.0/stock/%s/quote".format(ticker))
          .map(parseJson).foreach { jsonString => parseWithJson4s(jsonString) }

      }
    }
  }
  
}