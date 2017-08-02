package com.mm.marketgauge.web

import scala.concurrent.ExecutionContext
import scala.util.Random
import scala.io.StdIn
import com.mm.marketgauge.dao._
import com.mm.marketgauge.dao.{CompanyDao, SharePriceDao}
import akka.Done
import scala.concurrent.Future
import com.mm.marketgauge.entities.SharePrice

class MarketSectorHandler(config:com.typesafe.config.Config)(implicit val executionContext: ExecutionContext) extends com.mm.marketgauge.util.LogHelper {

  private val sharePriceDao = new SharePriceDao {
    val database =  new MongoDatabase{}
  }
  
  private val random = new Random()
  // (fake) async database query api
  def fetchItem(itemId: Long): Future[Option[Item]] = Future {
    Some(Item(s"foo:${random.nextInt(20)}", 11))
  }
  
  def fetchSharePrice(ticker:String):Future[Option[SharePrice]] = Future {
    sharePriceDao.findByTicker(ticker)
  }
  
  def saveOrder(order: Order): Future[Done] = Future { Done }

}
