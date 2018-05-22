package com.mm.marketgauge.service
import scala.util.{ Try, Success, Failure }
import com.mm.marketgauge.entities.SharePrice
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
      val head = dataDownloader.downloadCSV(sharePriceUrl.replace("<ticker>", ticker)).tail.head
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

}
