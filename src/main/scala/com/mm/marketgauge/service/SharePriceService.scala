package com.mm.marketgauge.service

import com.mm.marketgauge.entities.SharePrice
import scala.util.control.Exception.allCatch

/**
 * Service for handling Company data
 */
trait SharePriceService {
  private[service] val sharePriceUrl = "http://finance.yahoo.com/d/quotes.csv?s=<ticker>&f=sl1d1e7e8m4qr5s7"
  private[service] val dataDownloader:DataDownloader 
  private[service] val sectorService:SectorService
  
  def downloadSharePrice(ticker:String):SharePrice = {
    val head = dataDownloader.downloadCSV(sharePriceUrl.replace("<ticker>", ticker)).head
    extractData(head)
  }
  
  def loadPrices = {
    null
  }
  
  
  def persistSharePrices(sharePrices:List[SharePrice]):Int = { 0}
                
  private def extractData(dataList:List[String]) = {
    SharePrice(null, dataList(0), 
        dataList(1).toDouble,
        new java.text.SimpleDateFormat("MM/dd/yyyy").parse(dataList(2)),
        getDouble(dataList(3)),
        getDouble(dataList(4)),
        getDouble(dataList(5)),
        dataList(6), 
        getDouble(dataList(7)),
        getDouble(dataList(8)))
        
  }
    
  private def getDouble(doubleStr:String):Double = 
    allCatch opt doubleStr.toDouble match {
    case Some(doubleNum) => doubleNum
    case _ => Double.NaN
  }
  
}
