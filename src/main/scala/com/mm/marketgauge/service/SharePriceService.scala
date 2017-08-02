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
    private[service] val sharePriceUrl = "http://download.finance.yahoo.com/d/quotes.csv?s=<ticker>&f=sl1d1e7e8m4qr5s7j1"

    def downloadSharePrice(ticker: String): Option[SharePrice] = {
      val head = dataDownloader.downloadCSV(sharePriceUrl.replace("<ticker>", ticker)).head
      extractData(head).toOption
    }

    private def extractData(dataList: List[String]): Try[SharePrice] = {
      logger.debug(dataList.mkString("|"))
      Try {
        SharePrice(dataList(0),
          dataList(1).toDouble,
          dataList(2),
          getDouble(dataList(3)),
          getDouble(dataList(4)),
          getDouble(dataList(5)),
          dataList(6),
          getDouble(dataList(7)),
          getDouble(dataList(8)),
          dataList(9))
      }
    }
  }

}
