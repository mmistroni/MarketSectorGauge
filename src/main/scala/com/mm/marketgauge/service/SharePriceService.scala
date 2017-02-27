package com.mm.marketgauge.service

import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgauge.dao.SharePriceDao 
import scala.util.control.Exception.allCatch
import com.mm.marketgauge.util.Utilities.getDouble

/**
 * Service for handling Company data
 */
trait SharePriceService extends com.mm.marketgauge.util.LogHelper {
  private[service] val sharePriceUrl = "http://finance.yahoo.com/d/quotes.csv?s=<ticker>&f=sl1d1e7e8m4qr5s7j1"
  private[service] val dataDownloader:DataDownloader 
  private[service] val sharePriceDao:SharePriceDao
  
  def downloadSharePrice(ticker:String):Option[SharePrice] = {
    // Replace with Either
    try {
      val head = dataDownloader.downloadCSV(sharePriceUrl.replace("<ticker>", ticker)).head
      Some(extractData(head))
    } catch {
      case e:java.lang.Exception => {
        logger.info(s"FAiled to fetch prices for ticker $ticker:\n$e.")
        None
      }
    }
  }
  
  def loadPrices = {
    null
  }
  
  
  def persistSharePrices(sharePrices:Seq[SharePrice]):Int = { 
    sharePriceDao.insert(sharePrices : _*)
  }
                
  private def extractData(dataList:List[String]) = {
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
