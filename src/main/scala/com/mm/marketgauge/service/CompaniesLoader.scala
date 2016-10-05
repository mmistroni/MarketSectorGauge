package com.mm.marketgauge.service
import com.mm.marketgaugen.dao.{CompanyDao, SectorDao}
import com.mm.marketgauge.entities.{Company, CompanyRepo}
import com.mm.marketgauge.util.LogHelper
import com.typesafe.config._


object CompaniesLoader extends App with LogHelper {

  val downloader = new DataDownloader {}
  val conf = ConfigFactory.load()
  
  val companyService = new CompanyService {
    val dataDownloader = downloader
    val companyDao = new CompanyDao {
      val uri = conf.getString("db.uri")
    }
  }
  
  val sectorService = new SectorService {
    val dataDownloader = downloader
    val sectorDao = new SectorDao {
      val uri = conf.getString("db.uri")
    }

  }
  
  def persistRepos(repos:Seq[CompanyRepo]) = {
    companyService.persistCompanyRepos(repos)
  }

  def persistCompanies(companies:Seq[Company]) = {
    logger.info(s"Persisting ${companies.size}")
    companyService.persistCompanies(companies)
  }
  
  def loadRepos = {
    val allAmex = companyService.downloadAmexData
    logger.info(s"Got from Amex:${allAmex.size}")
    val allNasdaq = companyService.downloadNasdaqData
    logger.info(s"Got from Nasdaq:${allNasdaq.size}")
    val allNyse = companyService.downloadNyseData
    logger.info(s"Got from Nyse:${allNyse.size}")
    allAmex ::: allNasdaq ::: allNyse 
  }

  def loadCompanies = {
    val sectors = sectorService.getAllSectors
    logger.info(s"Got:${sectors.size}")
    val companies = sectors.flatMap(sector => companyService.downloadCompanyData(sector.sectorId))
    logger.info("OBtianed ${companies.size}")
    companies.filter(c => c != null)
  }
  
  def load = {
    logger.info(".... Loading...")
    val companies = loadCompanies
    logger.info(s"Obtained:${companies.size}")
    persistCompanies(companies)
    logger.info(s"... Loadig nasdaq..")
    val repos = loadRepos
    logger.info(s"Obtained ${repos.size}")
    persistRepos(repos)
    
    
  }
  
  
  load
  
  
  
}