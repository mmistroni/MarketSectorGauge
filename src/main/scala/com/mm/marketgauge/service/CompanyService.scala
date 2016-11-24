package com.mm.marketgauge.service
import com.mm.marketgauge.entities.Company
import com.mm.marketgauge.entities.CompanyRepo
import com.mm.marketgauge.dao.CompanyDao

/**
 * Service for handling Company data
 */
trait CompanyService {
  // sample sectorId http://biz.yahoo.com/p/csv/431conameu.csv
  private[service] val companyUrl = "https://biz.yahoo.com/p/csv/<sectorId>conameu.csv"
  private [service] val nasdaqCompaniesUrl = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nasdaq&render=download"
  private [service] val nyseCompaniesUrl = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=nyse&render=download"
  private [service] val amexCompaniesUrl = "http://www.nasdaq.com/screening/companies-by-name.aspx?letter=0&exchange=amex&render=download"
  private[service] val dataDownloader:DataDownloader 
  private[service] val companyDao:CompanyDao
  
  
  def downloadCompanyData(sectorId:Int):List[Company] = {
    val companies = _extractCsvData(companyUrl.replace("<sectorId>", sectorId.toString))
    companies.map(strList => Company.fromListOfString(strList::: List(sectorId.toString)))
  }
  
  def downloadNasdaqData():List[CompanyRepo] = {
    _extractCsvData(nasdaqCompaniesUrl).map(strList => CompanyRepo.fromListOfString(strList))
  }
  
  def downloadNyseData():List[CompanyRepo] = {
    _extractCsvData(nyseCompaniesUrl).map(strList => CompanyRepo.fromListOfString(strList))
  }
  
  def downloadAmexData():List[CompanyRepo] = {
    _extractCsvData(amexCompaniesUrl).map(strList => CompanyRepo.fromListOfString(strList))
  }
  
  
  def persistCompanies(companies:Seq[Company]):Int = {
    companyDao.insertCompanies(companies: _*)
  }
  
  def persistCompanyRepos(companyRepos:Seq[CompanyRepo]):Int = {
    companyDao.insertCompanyRepos(companyRepos: _*)
  }
 
  private def _extractCsvData(url:String):List[List[String]] = {
    // skipping the header
    dataDownloader.downloadCSV(url).tail
  }
  
  
}
