package com.marketgauge.integration

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures._
import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgauge.dao.SharePriceConverter
import com.mm.marketgauge.entities.SharePriceProperties._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgauge.dao.{ SharePriceDao, MongoDatabase, Database }

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import scala.collection.mutable.Stack
import com.github.simplyscala.MongoEmbedDatabase
import com.github.simplyscala.MongodProps



@RunWith(classOf[JUnitRunner])
class SharePriceDaoSuite extends FunSuite with MongoEmbedDatabase with BeforeAndAfter with Matchers {

  private var mongoProps: MongodProps = null
  private var sharePriceDao: SharePriceDao = null
  private val mongoPort = 4444
    
  before {
      mongoProps = mongoStart(port=mongoPort)   // by default port = 12345 & version = Version.2.3.0
      sharePriceDao = new SharePriceDao {
                        val database = new MongoDatabase {
                                            override val username = "test"
                                            override val password = "test"
                                            override val uri = s"mongodb://localhost:$mongoPort/"
                                            override val databaseName = "test"
                        }
                      }

  }
  
  after { mongoStop(mongoProps) }

  
  test("Inserting a SharePrice in the database") {
    
    val testSharePrice = new SharePrice( "testTicker", 1.0, 
                    "12/13/2016",
                     1.1, 2.0,
                     3.0, "i dont know",
                     4.0, 5.0,
                     "3B")
         
    val res = sharePriceDao.insert(List(testSharePrice))
    res shouldBe(1)
    
    val sharePriceFromDb = sharePriceDao.collection.find().map {
      item => SharePriceConverter.convertFromMongoObject(item) }.toList(0)
  
    sharePriceFromDb.asOfDate  shouldEqual(testSharePrice.asOfDate)
    sharePriceFromDb.exDivDate shouldEqual(testSharePrice.exDivDate)
    sharePriceFromDb.currentEps  shouldEqual(testSharePrice.currentEps)
    sharePriceFromDb.forwardEps shouldEqual(testSharePrice.forwardEps)
    sharePriceFromDb.movingAverage  shouldEqual(testSharePrice.movingAverage)
    sharePriceFromDb.peg shouldEqual(testSharePrice.peg)
    sharePriceFromDb.price  shouldEqual(testSharePrice.price)
    sharePriceFromDb.shortRatio shouldEqual(testSharePrice.shortRatio)
    sharePriceFromDb.ticker  shouldEqual(testSharePrice.ticker)
    sharePriceFromDb.marketCap  shouldEqual(testSharePrice.marketCap)  
      
  }
 
  
  
}