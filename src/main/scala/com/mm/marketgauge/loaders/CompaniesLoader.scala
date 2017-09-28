package com.mm.marketgauge.loaders

import com.mm.marketgauge.util.LogHelper
import com.typesafe.config._
import amazon.util.AWSClientFactory
import com.mm.marketgauge.service.CompanyServiceComponent
import com.mm.marketgauge.persistence._
import com.mm.marketgauge.persistence.mongo._

trait CompaniesLoader extends DataLoader with LogHelper {
  self:CompanyServiceComponent with PersistenceServiceComponent =>
  
  def loadRepos = {
    companyService.downloadAmexData ::: companyService.downloadNasdaqData ::: companyService.downloadNyseData
  }

  def loadCompanies = {
    val sectors = sectorRepository.getAll.map(sector => sector.sectorId)
    logger.info(s"Got:${sectors.size}")
    val companies = sectors.flatMap(sectorId => companyService.downloadCompanyData(sectorId))
    logger.info("OBtianed ${companies.size}")
    companies.filter(c => c != null)
  }
  
  def load = {
    logger.info(".... Loading...")
    val companies = loadCompanies
    val companiesCount = companyRepository.insert(companies)
    val repos = loadRepos
    logger.info(s"Obtained ${repos.size}")
    val reposCount = companyRepoRepository.insert(repos)
    logger.info("Publishing..")
    notify( "MarketSectorGauge. Companies upload", 
              s"$companiesCount companies were uploaded\n$reposCount companies repos were uploaded")
    
    companiesCount + reposCount
    
  }
  
}