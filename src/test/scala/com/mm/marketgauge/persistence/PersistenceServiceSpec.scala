package com.mm.marketgauge.persistence

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgauge.entities.{Sector, Company, CompanyRepo, SharePrice}
import com.mm.marketgauge.dao.{SectorDao, SharePriceDao, CompanyDao}


@RunWith(classOf[JUnitRunner])
class PersistenceServiceSpec extends FreeSpec with Matchers {
 
  val mockCompanyDao = Mockito.mock(classOf[CompanyDao])
  val mockSectorDao = Mockito.mock(classOf[SectorDao])
  val mockSharePriceDao = Mockito.mock(classOf[SharePriceDao])
  val persistenceService = 
    new PersistenceService {
            val sectorDao = mockSectorDao
            val companyDao = mockCompanyDao
            val sharePriceDao = mockSharePriceDao
                      }
  
  
  "The PersistenceService" - {
    "when calling getAllSectorIds should call sectorDao.getAllSectorIds" - {
      "returning a Sequence of Sectors" in {
      val expectedSectors = List(1,2,3)
       Mockito.when(mockSectorDao.getAllSectorIds).thenReturn(expectedSectors)
       val result = persistenceService.getAllSectorIds
       result shouldEqual(expectedSectors)
      }
    }
  }
  
  "The PersistenceService" - {
    "when calling storeSector should call sharePriceDao.insert" - {
      "and should return an integer representing number of inserts" in {
        
        val insertedCount = 22
        val testSectorList  = Seq(Sector("Oil & Gas Equipment & Services", 
                                    "^YHOh708", 124, "yhoo"))
        Mockito.when(mockSectorDao.insertBulk(testSectorList)).thenReturn(insertedCount)
        
        val rowCount = persistenceService.storeSectors(testSectorList)
        rowCount shouldEqual(insertedCount)
      }
    }
  }
  
  "The PersistenceService" - {
    "when calling storeCompany should call companyDao.insert" - {
      "and should return an integer representing number of inserts" in {
        
        val company1 = Company.fromListOfString(
            List("Financial", "0.622", "97700136.89B", "15.803", "8.204", "3.320", "127.027", "1.600", "17.599", "21.443", "1"))
                          
        val company2 = Company.fromListOfString(
            List("Test", "0.622", "97700136.89B", "15.803", "8.204", "3.320", "127.027", "1.600", "17.599", "21.443", "1"))
                          
        val companyList = List(company1, company2)
        
        val expectedInserts = companyList.size
        
        Mockito.when(mockCompanyDao.insertCompanies(companyList:_*)).thenReturn(expectedInserts)
        
        val res = persistenceService.storeCompanies(companyList)
        
        res shouldEqual(expectedInserts)
      }
    }
  }
  
  "The PersistenceService" - {
    "when calling storePrices should call storePriceDao.insert" - {
      "and should return an integer representing number of inserts" in {
        
        val sharePrices  =List(new SharePrice("ticker", 1.0, 
                    "12/13/2016",
                     Double.NaN, Double.NaN,
                     Double.NaN, "i dont know",
                     Double.NaN, Double.NaN, "1.0B"))
        val expectedInserts  =sharePrices.size
        
        Mockito.when(persistenceService.storePrices(sharePrices)).thenReturn(expectedInserts)
        
        val res = persistenceService.storePrices(sharePrices)
        res shouldEqual(res)
             
      }
    }
  }
  
  "The PersistenceService" - {
    "when calling storeCompanyRepo should call companyDao.insert" - {
      "and should return an integer representing number of inserts" in {
        
        val companyRepo = new CompanyRepo(
                null, "testTicker", "testName", 100.0,
                "1000.0", "1998", "MySector",
                "testIndustry") 
        
        val companyRepoList = List(companyRepo)
        
        val expectedInserts = companyRepoList.size
        
        Mockito.when(mockCompanyDao.insertCompanyRepos(companyRepoList:_*)).thenReturn(expectedInserts)
        
        val res = persistenceService.storeCompanyRepos(companyRepoList)
        
        res shouldEqual(expectedInserts)
      }
    }
  }
   
   
  
}