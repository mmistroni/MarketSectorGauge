package com.mm.marketgauge.service
import scala.util.{ Try, Success, Failure }
import com.mm.marketgauge.entities.{SharePrice, StockData}
import com.mm.marketgauge.dao.SharePriceDao
import scala.util.control.Exception.allCatch
import com.mm.marketgauge.util.Utilities.getDouble

/**
 * Service for handling Company data
 */



trait SharePriceServiceComponent {
  this: DataDownloaderComponent =>

  def sharePriceService = new SharePriceService()

  class SharePriceService {                
    private[service] val sharePriceUrl = 
       "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=<ticker>&interval=1min&apikey=3K8QCCZYO839KMOU&datatype=csv"

    def downloadSharePrice(ticker: String): Option[SharePrice] = {
      val urlString = sharePriceUrl.replace("<ticker>", ticker)
      val head = dataDownloader.downloadCSV(urlString).tail.head
      extractData(head, ticker).toOption
    }
    
    private def extractData(dataList: List[String], ticker:String): Try[SharePrice] = {
      logger.debug(dataList.mkString("|"))
      Try {
        SharePrice(ticker,
          dataList(4).toDouble,
          dataList(0),
          getDouble(dataList(3)),
          getDouble(dataList(4)),
          getDouble(dataList(5)),
          "N/A",
          0.0,
          0.0,
          "N/A")
      }
    }
    
    
  }
  
  class JsonSharePriceService extends SharePriceService {
    import org.json4s._
    import org.json4s.native.JsonMethods._
    implicit val formats = DefaultFormats
    
    
    override private[service] val sharePriceUrl = 
       "https://api.iextrading.com/1.0/stock/<ticker>/quote";
  
    
    override def downloadSharePrice(ticker: String): Option[SharePrice] = {
      val urlString = sharePriceUrl.replace("<ticker>", ticker)
      downloadFunction(urlString)(dataDownloader)
    }
    
    private[service] def downloadFunction(url:String):DataDownloader => Option[SharePrice] = {
        dataDownloader => {
            val json = dataDownloader.downloadJson(url)
            val stockData = json.extract[StockData]
            Try {
                  SharePrice(stockData.symbol,
                    stockData.latestPrice,
                    stockData.latestTime,
                    0.0,
                    0.0,
                    stockData.week52High,
                    "",
                    getDouble(stockData.peRatio),
                    0.0,
                    stockData.marketCap.toString)
                    
                }.toOption
          
          }
          
    }
 }
  
}

trait JsonSharePriceServiceComponent extends SharePriceServiceComponent {
  this: DataDownloaderComponent =>
  override def sharePriceService = new JsonSharePriceService()

}

