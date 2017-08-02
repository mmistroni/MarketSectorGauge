package com.mm.marketgauge.persistence

import com.mm.marketgauge.entities.{ Sector, Company, CompanyRepo, SharePrice }
import com.mm.marketgauge.dao._

/**
 * Global Persistence Service for the application
 * // Need to be refactored. database will be injected
 */

trait PersistenceService {

  /**
     *  @return all sectorsIds
     */

    def getAllSectorIds: Seq[Int]

    /**
     * @return all sectors
     */
    def getAllSectors: Seq[Sector]

    /**
     * Stores a Sector in the database
     * @param sectorList a List of Sector objects
     */
    def storeSectors(sectorList: Seq[Sector]): Int
    /**
     * Stores a company in the database
     * @param company a Company object
     */
    def storeCompanies(companies: Seq[Company]): Int 
    /**
     * Store a price
     * @param prices a List of SharePrice object
     */
    def storePrices(prices: Seq[SharePrice]): Int 

    /**
     * Store companyRepos
     * @param companyRepos a list of companyRepos object
     */
    def storeCompanyRepos(companyRepos: Seq[CompanyRepo]): Int 

}


trait PersistenceServiceComponent {

  // This needs better abstractions....
  
  val persistenceService:PersistenceService = new MongoPersistenceService()

  
}

class MongoPersistenceService extends PersistenceService  {
   private lazy val mongoDb = new MongoDatabase()
    val companyDao:CompanyDao = new CompanyDao {
      lazy val database = mongoDb
    }

    val sectorDao:SectorDao = new SectorDao {
      lazy val database = mongoDb
    }

    val sharePriceDao:SharePriceDao = new SharePriceDao {
      lazy val database = mongoDb
    }

    val companyRepoDao:CompanyRepoDao = new CompanyRepoDao {
      lazy val database = mongoDb
    }

    def getAllSectorIds: Seq[Int] = getAllSectors.map(sector => sector.sectorId)

    def getAllSectors: Seq[Sector] = sectorDao.getAll

    def storeSectors(sectorList: Seq[Sector]): Int = sectorDao.insert(sectorList)
    
    def storeCompanies(companies: Seq[Company]): Int = companyDao.insert(companies)
    
    def storePrices(prices: Seq[SharePrice]): Int = sharePriceDao.insert(prices)

    def storeCompanyRepos(companyRepos: Seq[CompanyRepo]): Int = companyRepoDao.insert(companyRepos)

  }



