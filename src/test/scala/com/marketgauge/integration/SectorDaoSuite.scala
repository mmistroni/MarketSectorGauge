package com.marketgauge.integration

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures._
import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.dao.SectorConverter
import com.mm.marketgauge.entities.SectorProperties._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgauge.dao.{ SectorDao, MongoDatabase, Database }

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import scala.collection.mutable.Stack
import com.github.simplyscala.MongoEmbedDatabase
import com.github.simplyscala.MongodProps



@RunWith(classOf[JUnitRunner])
class SectorDaoSuite extends FunSuite with MongoEmbedDatabase with BeforeAndAfter with Matchers {

  private var mongoProps: MongodProps = null
  private var sectorDao: SectorDao = null
  private val testName = "sectorName"
  private val testTicker = "sectorTicker"
  private val sectorId = -1
  private val source = "sectorSource"
    
  before {
      mongoProps = mongoStart()   // by default port = 12345 & version = Version.2.3.0
      sectorDao = new SectorDao {
                        val database = new MongoDatabase {
                                            val username = "test"
                                            val password = "test"
                                            val uri = "mongodb://localhost:12345/"
                                            val databaseName = "test"
                        }
                      }

  }
  
  after { mongoStop(mongoProps) }

  
  private def createSector = new Sector(testName, testTicker, sectorId, source) 
  
  test("Inserting a Sector in the database") {
    
    val testSector = createSector 
    val res = sectorDao.insertBulk(List(testSector))
    res shouldBe(1)
  }

  
  test("Finding all sector Ids") {
    val testSector = createSector 
    val res = sectorDao.insertBulk(List(testSector))
    
    val sectorsIds = sectorDao.getAllSectorIds
    sectorsIds(0) shouldEqual(testSector.sectorId)
  }
  
  test("Finding all sectors") {
    val testSector = createSector 
    val res = sectorDao.insertBulk(List(testSector))
    
    val sectors = sectorDao.findAll.toList
    
    sectors.size shouldEqual(1)
    
    val persistedSectors = sectors(0)
    persistedSectors.name shouldEqual(testSector.name)
    persistedSectors.sectorId shouldEqual(testSector.sectorId)
    persistedSectors.source shouldEqual(testSector.source)
    persistedSectors.ticker shouldEqual(testSector.ticker)
  }
  
  test("Updating  sectors") {
    val testSector = createSector
    val res = sectorDao.insertBulk(List(testSector))
    val sectors = sectorDao.findAll.toList
    sectors.size shouldEqual(1)
    
    val updatedSector = testSector.copy(ticker="Updated")
    
    val updatesCount = sectorDao.insertBulk(List(updatedSector))
    updatesCount shouldEqual(1)
    
    val updatedSectors = sectorDao.findAll.toList
    
    val persistedSectors = updatedSectors(0)
    persistedSectors.name shouldEqual(updatedSector.name)
    persistedSectors.sectorId shouldEqual(updatedSector.sectorId)
    persistedSectors.source shouldEqual(updatedSector.source)
    persistedSectors.ticker shouldEqual(updatedSector.ticker)
  }
  
  
  
}