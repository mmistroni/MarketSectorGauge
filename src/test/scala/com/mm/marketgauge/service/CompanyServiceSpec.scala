package com.mm.marketgauge.service

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mm.marketgauge.entities.{Company, CompanyRepo}
import com.mm.marketgaugen.dao.CompanyDao



@RunWith(classOf[JUnitRunner])
class CompanyServiceSpec extends FreeSpec with Matchers {
 
  val mockDownloader = Mockito.mock(classOf[DataDownloader])
  val mockCompanyDao = Mockito.mock(classOf[CompanyDao])
  val mockCompanyService = 
    new CompanyService {
            val dataDownloader = mockDownloader
            val companyDao = mockCompanyDao
                      }
  "The CompanyService" - {
    "when calling downloadCopmpayData with a SectorId should call dataDownloader.downloadFromURL with companyUrl" - {
      "should return a Company" in {
        
        val sectorId = -1;
        
        val yahooData = List(
                List("Description", "1-Day Price Chg %", "Market Cap", "P/E", "ROE %", "Div. Yield %", 
                      "Debt to Equity", "Price to Book", "Net Profit Margin (mrq)", "Price To Free Cash Flow (mrq)"),
                List("Financial", "0.622", "97700136.89B", "15.803", "8.204", "3.320", "127.027", "1.600", "17.599", "21.443")
                            )
        val companyUrl = mockCompanyService.companyUrl.replace("<sectorId>", sectorId.toString)
                        
        Mockito.when(mockDownloader.downloadCSV(companyUrl)).thenReturn(yahooData)
        val companyResult = mockCompanyService.downloadCompanyData(sectorId).head
        companyResult.name should be (yahooData(1)(0))
        companyResult.sectorId should be (sectorId)
        companyResult.divYield should be (yahooData(1)(5).toDouble)
        
        Mockito.verify(mockDownloader).downloadCSV(companyUrl)
        
        
      }
    }
  }
  
  
  "The CompanyService" - {
    "when calling downloadNasdaqData  should call dataDownloader.downloadCSV with nasdaqUrl" - {
      "should return a List of Company" in {
        
        val nasdaqData =  List(
                          List("Symbol", "Name", "LastSale", "MarketCap", "IPOyear", "Sector", "industry", "Summary Quote", ""),
                          List("DDD", "3D Systems Corporation", "15.46", "$1.73B", "n/a", "Technology", "Computer Software: Prepackaged Software", "http://www.nasdaq.com/symbol/ddd", "") 
                        )
                          
        Mockito.when(mockDownloader.downloadCSV(mockCompanyService.nasdaqCompaniesUrl)).thenReturn(nasdaqData)
        
        val companyResult = mockCompanyService.downloadNasdaqData().head
        companyResult.ticker should be (nasdaqData(1)(0))
        companyResult.name   should be (nasdaqData(1)(1))
        companyResult.lastSale should be (nasdaqData(1)(2).toDouble)
        Mockito.verify(mockDownloader).downloadCSV(mockCompanyService.nasdaqCompaniesUrl)
        
      }
    }
  }
  
  "The CompanyService" - {
    "when calling downloadNyseData  should call dataDownloader.downloadCSV with nyseCompaniesUrl" - {
      "should return a List of CompanyRepo" in {
        
        val nyseData =  List(
                        List("Symbol", "Name", "LastSale", "MarketCap", "IPOyear", "Sector", "industry", "Summary Quote", ""),
                        List("MMM", "3M Company", "178.82", "$108.08B", "n/a", "Health Care", "Medical/Dental Instruments", "http://www.nasdaq.com/symbol/mmm", ""))
                          
        Mockito.when(mockDownloader.downloadCSV(mockCompanyService.nyseCompaniesUrl)).thenReturn(nyseData)
        
        val companyResult = mockCompanyService.downloadNyseData().head
        companyResult.ticker should be (nyseData(1)(0))
        companyResult.name   should be (nyseData(1)(1))
        companyResult.lastSale should be (nyseData(1)(2).toDouble)
        Mockito.verify(mockDownloader).downloadCSV(mockCompanyService.nyseCompaniesUrl)
        
      }
    }
  }
  
  
  "The CompanyService" - {
    "when calling downloadAmexData  should call dataDownloader.downloadCSV with amexCompaniesUrl" - {
      "should return a List of CompanyRepo" in {
        
        val amexData =  List(
                        List("Symbol", "Name", "LastSale", "MarketCap", "IPOyear", "Sector", "industry", "Summary Quote", ""),
                        List("WBAI", "500.com Limited", "17.74", "$734.06M", "2013", "Consumer Services", "Services-Misc. Amusement & Recreation", 
                            "http://www.nasdaq.com/symbol/wbai","" ))
                          
        Mockito.when(mockDownloader.downloadCSV(mockCompanyService.amexCompaniesUrl)).thenReturn(amexData)
        
        val companyResult = mockCompanyService.downloadAmexData().head
        companyResult.ticker should be (amexData(1)(0))
        companyResult.name   should be (amexData(1)(1))
        companyResult.lastSale should be (amexData(1)(2).toDouble)
        Mockito.verify(mockDownloader).downloadCSV(mockCompanyService.amexCompaniesUrl)
        
      }
    }
  }
  
  
  "The CompanyService" - {
    "when calling persistCompanies  should call CompanyDao" - {
      "and return an Integer" in {
        
        val testCompany = Company.fromListOfString(
            List("Financial", "0.622", "97700136.89B", "15.803", "8.204", "3.320", "127.027", "1.600", "17.599", "21.443", "1")
                                                  )
        val companies = List(testCompany)          
        val numInsert =  companies.size
                            
        Mockito.when(mockCompanyDao.insertCompanies(companies: _*)).thenReturn(numInsert)
        val res = mockCompanyService.persistCompanies(companies)
        res should be(numInsert)
        Mockito.verify(mockCompanyDao).insertCompanies(companies: _*)
        
      }
    }
  }
  
  
  "The CompanyService" - {
    "when calling persistCompanyRepo  should call CompanyDao" - {
      "and return an Integer" in {
        
        val testCompanyRepo = CompanyRepo.fromListOfString(
            List("WBAI", "500.com Limited", "17.74", "$734.06M", "2013", "Consumer Services", "Services-Misc. Amusement & Recreation", 
                            "http://www.nasdaq.com/symbol/wbai","" )
                                                          )
        val companyRepos = List(testCompanyRepo)          
        val numInsert =  companyRepos.size
                            
        Mockito.when(mockCompanyDao.insertCompanyRepos(companyRepos: _*)).thenReturn(numInsert)
        val res = mockCompanyService.persistCompanyRepos(companyRepos)
        res should be(numInsert)
        Mockito.verify(mockCompanyDao).insertCompanyRepos(companyRepos: _*)
        
        
      }
    }
  }
  
  
  
}