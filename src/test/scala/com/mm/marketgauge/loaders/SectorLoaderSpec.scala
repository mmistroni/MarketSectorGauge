package com.mm.marketgauge.loaders

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgauge.entities.{ Sector, Company, CompanyRepo, SharePrice }
import com.mm.marketgauge.service._
import com.mm.marketgauge.persistence._

@RunWith(classOf[JUnitRunner])
class SectorLoaderSpec extends FreeSpec with Matchers {

  val mockCompanyRepository = Mockito.mock(classOf[BaseCompanyRepository])
  val mockCompanyRepoRepository = Mockito.mock(classOf[BaseCompanyRepoRepository])
  val mockSectorRepository = Mockito.mock(classOf[BaseSectorRepository])
  val mockSharePriceRepository = Mockito.mock(classOf[BaseSharePriceRepository])
  val mockNotifier = Mockito.mock(classOf[Notifier])

  trait MockPersistenceServiceComponent extends PersistenceServiceComponent {

    override val sectorRepository = mockSectorRepository

    override val companyRepository = mockCompanyRepository

    override val companyRepoRepository = mockCompanyRepoRepository

    override val sharePriceRepository = mockSharePriceRepository
    
    
  }

  trait MockSectorServiceComponent extends SectorServiceComponent with DataDownloaderComponent {
    override val dataService = Mockito.mock(classOf[SectorService])
  }

  trait MockSectorLoader extends SectorLoader with MockPersistenceServiceComponent
    with MockSectorServiceComponent {

    override val notifier = mockNotifier
  }
  
  val mockSectorLoader = new MockSectorLoader {}

  "The MockSectorLoader" - {
    "when calling fetchSectorIds should " - {
      "return call the Mock dataService" in {

        val expectedSectorsIds = Seq(1, 2, 3)
        Mockito.when(mockSectorLoader.dataService.downloadAllSectorsIds).thenReturn(expectedSectorsIds)
        val result = mockSectorLoader.fetchSectorIds
        result shouldEqual (expectedSectorsIds)

      }
    }
  }

  "The MockSectorLoader" - {
    "when calling downloadSectors should " - {
      "return call the Mock dataService" in {

        val expectedSectorsIds = Seq(1, 2)
        val mockSect1 = Mockito.mock(classOf[Sector])
        val mockSect2 = Mockito.mock(classOf[Sector])
        val expectedSectors = Seq(mockSect1, mockSect2)

        Mockito.when(mockSectorLoader.dataService.downloadSectorData(expectedSectorsIds(0))).thenReturn(Some(mockSect1))
        Mockito.when(mockSectorLoader.dataService.downloadSectorData(expectedSectorsIds(1))).thenReturn(Some(mockSect2))
        val result = mockSectorLoader.downloadSectors(expectedSectorsIds)
        result shouldEqual (expectedSectors)

      }
    }
  }

  
  "The MockSectorLoader" - {
    "when calling fetchSectors should " - {
      "call the fetchSectorIds and downloadSectors" in {

        val expectedSectorsIds = Seq(1, 2)
        val mockSect1 = Mockito.mock(classOf[Sector])
        val mockSect2 = Mockito.mock(classOf[Sector])
        val expectedSectors = Seq(mockSect1, mockSect2)

        Mockito.when(mockSectorLoader.dataService.downloadAllSectorsIds).thenReturn(expectedSectorsIds)
        Mockito.when(mockSectorLoader.dataService.downloadSectorData(expectedSectorsIds(0))).thenReturn(Some(mockSect1))
        Mockito.when(mockSectorLoader.dataService.downloadSectorData(expectedSectorsIds(1))).thenReturn(Some(mockSect2))
        val result = mockSectorLoader.fetchSectors
        result shouldEqual (expectedSectors)

      }
    }
  }
  
  "The MockSectorLoader" - {
    "when calling load should " - {
      "call the dataService and mockSectorRepository" in {

        val expectedSectorsIds = Seq(1,2)
        val mockSect1 = Mockito.mock(classOf[Sector])
        val mockSect2 = Mockito.mock(classOf[Sector])
        val expectedSectors = Seq(mockSect1, mockSect2)
        
        Mockito.when(mockSectorLoader.dataService.downloadAllSectorsIds).thenReturn(expectedSectorsIds)
        Mockito.when(mockSectorLoader.dataService.downloadSectorData(expectedSectorsIds(0))).thenReturn(Some(mockSect1))
        Mockito.when(mockSectorLoader.dataService.downloadSectorData(expectedSectorsIds(1))).thenReturn(Some(mockSect2))
        Mockito.when(mockSectorLoader.sectorRepository.insert(expectedSectors)).thenReturn(expectedSectors.size)
        val result = mockSectorLoader.load
        result shouldEqual (expectedSectors)

      }
    }
  }
  
  
  /**
   *
   *
   * "The PersistenceService" - {
   * "when calling getAllSector should call sectorDao.getAll" - {
   * "returning a Sequence of Sectors" in {
   *
   * val mockSect1 = Mockito.mock(classOf[Sector])
   * val mockSect2 = Mockito.mock(classOf[Sector])
   * val expectedSectors = Seq(mockSect1, mockSect2)
   * Mockito.when(mockSectorDao.getAll).thenReturn(expectedSectors)
   * val result = persistenceService.getAllSectors
   * result shouldEqual(expectedSectors)
   * }
   * }
   * }
   *
   *
   *
   *
   *
   * "The PersistenceService" - {
   * "when calling storeSector should call sharePriceDao.insert" - {
   * "and should return an integer representing number of inserts" in {
   *
   * val insertedCount = 22
   * val testSectorList  = Seq(Sector("Oil & Gas Equipment & Services",
   * "^YHOh708", 124, "yhoo"))
   * Mockito.when(mockSectorDao.insert(testSectorList)).thenReturn(insertedCount)
   *
   * val rowCount = persistenceService.storeSectors(testSectorList)
   * rowCount shouldEqual(insertedCount)
   * }
   * }
   * }
   *
   * "The PersistenceService" - {
   * "when calling storeCompany should call companyDao.insert" - {
   * "and should return an integer representing number of inserts" in {
   *
   * val company1 = Company.fromListOfString(
   * List("Financial", "0.622", "97700136.89B", "15.803", "8.204", "3.320", "127.027", "1.600", "17.599", "21.443", "1"))
   *
   * val company2 = Company.fromListOfString(
   * List("Test", "0.622", "97700136.89B", "15.803", "8.204", "3.320", "127.027", "1.600", "17.599", "21.443", "1"))
   *
   * val companyList = List(company1, company2)
   *
   * val expectedInserts = companyList.size
   *
   * Mockito.when(mockCompanyDao.insert(companyList)).thenReturn(expectedInserts)
   *
   * val res = persistenceService.storeCompanies(companyList)
   *
   * res shouldEqual(expectedInserts)
   * }
   * }
   * }
   *
   * "The PersistenceService" - {
   * "when calling storePrices should call storePriceDao.insert" - {
   * "and should return an integer representing number of inserts" in {
   *
   * val sharePrices  =List(new SharePrice("ticker", 1.0,
   * "12/13/2016",
   * Double.NaN, Double.NaN,
   * Double.NaN, "i dont know",
   * Double.NaN, Double.NaN, "1.0B"))
   * val expectedInserts  =sharePrices.size
   *
   * Mockito.when(mockSharePriceDao.insert(sharePrices)).thenReturn(expectedInserts)
   *
   * val res = persistenceService.storePrices(sharePrices)
   * res shouldEqual(res)
   *
   * }
   * }
   * }
   *
   * "The PersistenceService" - {
   * "when calling storeCompanyRepo should call companyDao.insert" - {
   * "and should return an integer representing number of inserts" in {
   *
   * val companyRepo = new CompanyRepo(
   * null, "testTicker", "testName", 100.0,
   * "1000.0", "1998", "MySector",
   * "testIndustry")
   *
   * val companyRepoList = List(companyRepo)
   *
   * val expectedInserts = companyRepoList.size
   *
   * Mockito.when(mockCompanyRepoDao.insert(companyRepoList)).thenReturn(expectedInserts)
   *
   * val res = persistenceService.storeCompanyRepos(companyRepoList)
   *
   * res shouldEqual(expectedInserts)
   * }
   * }
   * }
   */

}