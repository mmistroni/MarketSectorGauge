package com.marketgauge.integration

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures._
import com.mm.marketgauge.entities.{Company, CompanyRepo}
import com.mm.marketgauge.dao.{CompanyRepoConverter}
import com.mm.marketgauge.dao.CompanyConverter
import com.mm.marketgauge.entities.CompanyProperties._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgauge.dao.{ CompanyRepoDao, MongoDatabase, Database }
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import scala.collection.mutable.Stack
import com.github.simplyscala.MongoEmbedDatabase
import com.github.simplyscala.MongodProps



@RunWith(classOf[JUnitRunner])
class CompanyRepoDaoSuite extends FunSuite with MongoEmbedDatabase with BeforeAndAfter with Matchers {

  private var mongoProps: MongodProps = null
  private var companyRepoDao: CompanyRepoDao = null
  private val testName = "sectorName"
  private val testTicker = "sectorTicker"
  private val sectorId = -1
  private val source = "sectorSource"
  private val mongoPort = 2222
    
  before {
      mongoProps = mongoStart(port=mongoPort)   // by default port = 12345 & version = Version.2.3.0
      companyRepoDao = new CompanyRepoDao {
                        val database = new MongoDatabase {
                                            override val username = "test"
                                            override val password = "test"
                                            override val uri = s"mongodb://localhost:$mongoPort/"
                                            override val databaseName = "test"
                        }
                      }

  }
  
  after { mongoStop(mongoProps) }

  
  private def createCompanyRepo = 
            new CompanyRepo(
                null, "testTicker", "testName", 100.0,
                "1000.0", "1998", "MySector",
                "testIndustry") 
  
  
  test("Inserting a CompanyRepo in the database") {
    
    val testCompany = createCompanyRepo 
    val res = companyRepoDao.insert(List(testCompany, testCompany))
    res shouldBe(2)
  }

  
  
  test("Updating a CompanyRepo ") {
    val testCompanyRepo = createCompanyRepo
    val res = companyRepoDao.insert(List(testCompanyRepo))
    res shouldEqual(1)
    
    val companyRepo = companyRepoDao.repoCollection.find().map {
      item => CompanyRepoConverter.convertFromMongoObject(item) }.toList(0)
  
    companyRepo.ticker shouldEqual(testCompanyRepo.ticker)
    companyRepo.industry shouldEqual(testCompanyRepo.industry)
    companyRepo.ipoYear shouldEqual(testCompanyRepo.ipoYear)
    companyRepo.lastSale shouldEqual(testCompanyRepo.lastSale)
    companyRepo.marketCap shouldEqual(testCompanyRepo.marketCap)
    companyRepo.name shouldEqual(testCompanyRepo.name)
      
      
    val updatedCompanyRepo = testCompanyRepo.copy(industry="TestIndustry",name="UpdatedName")
    
    val updatesCount = companyRepoDao.insert(List(updatedCompanyRepo))
    updatesCount shouldEqual(1)
    
    val companyRepoFromDb = companyRepoDao.repoCollection.find().map {
      item => CompanyRepoConverter.convertFromMongoObject(item) }.toList(0)
  
    println("Commpany repo from db is:"+ companyRepoFromDb)  
      
    companyRepoFromDb.name shouldEqual(updatedCompanyRepo.name)
    companyRepoFromDb.industry shouldEqual(updatedCompanyRepo.industry)
    companyRepoFromDb.ticker shouldEqual(testCompanyRepo.ticker)
    
    companyRepoFromDb.ipoYear shouldEqual(testCompanyRepo.ipoYear)
    companyRepoFromDb.lastSale shouldEqual(testCompanyRepo.lastSale)
    companyRepoFromDb.marketCap shouldEqual(testCompanyRepo.marketCap)
    
    
  }
  
  
  
  
}