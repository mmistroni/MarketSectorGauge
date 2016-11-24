package com.mm.marketgauge.persistence

import com.mm.marketgauge.entities.{Sector, Company, CompanyRepo, SharePrice}
import com.mm.marketgauge.dao._


/**
 * Global Persistence Service for the application
 */
trait PersistenceService {
 
  val sectorDao:SectorDao
  val sharePriceDao:SharePriceDao
  val companyDao:CompanyDao
  
  
  /** 
   *  @return all sectors
   */
  
  def getAllSectorIds:Seq[Int] = sectorDao.getAllSectorIds
  
  /**
   * Stores a Sector in the database
   * @param sectorList a List of Sector objects
   */
  def storeSectors(sectorList:Seq[Sector]):Int = sectorDao.insertBulk(sectorList)
  /**
   * Stores a company in the database
   * @param company a Company object
   */
  def storeCompanies(companies:Seq[Company]):Int = companyDao.insertCompanies(companies:_*)
  /**
   * Store a price
   * @param prices a List of SharePrice object
   */
  def storePrices(prices:Seq[SharePrice]):Int = sharePriceDao.insert(prices:_*)
  
  /**
   * Store companyRepos
   * @param companyRepos a list of companyRepos object
   */
  def storeCompanyRepos(companyRepos:Seq[CompanyRepo]):Int = companyDao.insertCompanyRepos(companyRepos:_*)
  
  
}