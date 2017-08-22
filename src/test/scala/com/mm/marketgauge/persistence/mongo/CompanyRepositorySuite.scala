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
import com.mm.marketgauge.persistence.mongo.CompanyRepository
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import scala.collection.mutable.Stack
import com.github.simplyscala.MongoEmbedDatabase
import com.github.simplyscala.MongodProps



@RunWith(classOf[JUnitRunner])
class CompanyRepositorySuite extends FunSuite with MongoEmbedDatabase with BeforeAndAfter with Matchers {

  private var mongoProps: MongodProps = null
  private var companyRepository: CompanyRepository = null
  private val testName = "sectorName"
  private val testTicker = "sectorTicker"
  private val sectorId = -1
  private val source = "sectorSource"
  private val mongoPort = 7651
    
  before {
      mongoProps = mongoStart(port=mongoPort)   // by default port = 12345 & version = Version.2.3.0
      companyRepository = new CompanyRepository {
      override lazy val client = MongoClient(MongoClientURI(s"mongodb://localhost:$mongoPort/"))("test")
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
    val res = companyRepository.insert(Seq(testCompany))
    res shouldBe(1)
  }

  test("Updating a Company ") {
    val testCompany = createCompany
    val res = companyRepository.insert(Seq(testCompany))
    res shouldEqual(1)
    
    val company = companyRepository.getAll.toList(0)
  
    company.name shouldEqual(testCompany.name)
    company.sectorId shouldEqual(testCompany.sectorId)
    
    val updatedCompany = testCompany.copy(sectorId=23)
    
    val updatesCount = companyRepository.insert(Seq(updatedCompany))
    updatesCount shouldEqual(1)
    
    val companyFromDb = companyRepository.getAll.toList(0)
  
    companyFromDb.name shouldEqual(updatedCompany.name)
    companyFromDb.sectorId shouldEqual(updatedCompany.sectorId)
  }
  
  
  
  
}