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
import com.mm.marketgauge.dao.{ CompanyDao, MongoDatabase, Database }
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import scala.collection.mutable.Stack
import com.github.simplyscala.MongoEmbedDatabase
import com.github.simplyscala.MongodProps



@RunWith(classOf[JUnitRunner])
class CompanyDaoSuite extends FunSuite with MongoEmbedDatabase with BeforeAndAfter with Matchers {

  private var mongoProps: MongodProps = null
  private var companyDao: CompanyDao = null
  private val testName = "sectorName"
  private val testTicker = "sectorTicker"
  private val sectorId = -1
  private val source = "sectorSource"
  private val mongoPort = 1111
    
  before {
      mongoProps = mongoStart(port=mongoPort)   // by default port = 12345 & version = Version.2.3.0
      companyDao = new CompanyDao {
                        val database = new MongoDatabase {
                                            override val username = "test"
                                            override val password = "test"
                                            override val uri = s"mongodb://localhost:$mongoPort/"
                                            override val databaseName = "test"
                        }
                      }

  }
  
  after { mongoStop(mongoProps) }

  
  private def createCompany = Company(null, "name" , -1.0, 1000.0, 
                    -4.1, -5.2, -6.1, 
                    -7.1, -3.0, -9.1, 
                    -4.0, -99)
         
  private def createCompanyRepo = 
            new CompanyRepo(
                null, "testTicker", "testName", 100.0,
                "1000.0", "1998", "MySector",
                "testIndustry") 
  
  test("Inserting a Company in the database") {
    
    val testCompany = createCompany 
    val res = companyDao.insertCompanies(List(testCompany):_*)
    res shouldBe(1)
  }

  test("Updating a Company ") {
    val testCompany = createCompany
    val res = companyDao.insertCompanies(List(testCompany):_*)
    res shouldEqual(1)
    
    val company = companyDao.companyCollection.find().map {
      item => CompanyConverter.convertFromMongoObject(item) }.toList(0)
  
    company.name shouldEqual(testCompany.name)
    company.sectorId shouldEqual(testCompany.sectorId)
    
    val updatedCompany = testCompany.copy(sectorId=23)
    
    val updatesCount = companyDao.insertCompanies(List(updatedCompany):_*)
    updatesCount shouldEqual(1)
    
    val companyFromDb = companyDao.companyCollection.find().map {
      item => CompanyConverter.convertFromMongoObject(item) }.toList(0)
  
    companyFromDb.name shouldEqual(updatedCompany.name)
    companyFromDb.sectorId shouldEqual(updatedCompany.sectorId)
  }
  
  
  
  
}