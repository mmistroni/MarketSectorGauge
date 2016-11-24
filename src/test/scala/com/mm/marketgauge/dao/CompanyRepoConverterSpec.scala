package com.mm.marketgauge.dao

import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.scalatest.junit.JUnitRunner

import com.mm.marketgauge.entities.CompanyRepo
import com.mm.marketgauge.entities.CompanyRepoProperties.INDUSTRY
import com.mm.marketgauge.entities.CompanyRepoProperties.IPOYEAR
import com.mm.marketgauge.entities.CompanyRepoProperties.LASTSALE
import com.mm.marketgauge.entities.CompanyRepoProperties.MARKETCAP
import com.mm.marketgauge.entities.CompanyRepoProperties.NAME
import com.mm.marketgauge.entities.CompanyRepoProperties.SECTOR
import com.mm.marketgauge.entities.CompanyRepoProperties.TICKER

@RunWith(classOf[JUnitRunner])
class CompanyRepoConverterSpec extends FreeSpec with Matchers {
                      
  "The CompanyRepoConverter" - {
    "when calling convertToMongoObject with a CompanyRepo object " - {
      "should return a DbObject with all properties set" in {
        val testName = "sectorName"
        val testTicker ="sectorTicker"
        val testLastSale = 100.0
        val testMarketCap  = "1000.0"
        val testIpoYear  = "1998"
        val testSector  ="N.A"
        val testIndustry =  "N.a"
        
        val testCompanyRepo = new CompanyRepo(
                null, testTicker, testName, testLastSale,
                testMarketCap, testIpoYear, testSector,
                testIndustry) 
        
        val dbObject = CompanyRepoConverter.convertToMongoObject(testCompanyRepo)
        dbObject.containsField(NAME) should be (true)
        dbObject.containsField(TICKER) should be (true)
        dbObject.containsField(LASTSALE) should be (true)
        dbObject.containsField(MARKETCAP) should be (true)
        dbObject.containsField(IPOYEAR) should be (true)
        dbObject.containsField(SECTOR) should be (true)
        dbObject.containsField(INDUSTRY) should be (true)
        
        
        dbObject.get(NAME).toString should be (testName)
        dbObject.get(TICKER).toString should be (testTicker)
        dbObject.get(LASTSALE).asInstanceOf[Double] should be (testLastSale)
        dbObject.get(MARKETCAP).toString should be (testMarketCap)
        dbObject.get(IPOYEAR).toString should be (testIpoYear)
        dbObject.get(SECTOR).toString should be (testSector)
        dbObject.get(INDUSTRY).toString should be (testIndustry)
        
      }
    }
  }
  

  
  /** On HOld. need to find out how to mock it
  "The SectorConverter" - {
    "when calling convertFromMongoObject with a DbSector object " - {
      "should return a Sector with all properties set" in {
        val testName = "sectorName"
        val testTicker ="sectorTicker"
        val testSectorId = -1
        val testSource = "sectorSource"
        
        val mockDbObject = Mockito.mock(classOf[DBObject])
        //when(mockDbObject.getAs[ObjectId](ID)).thenReturn(Option[com.mongodb.casbah.Imports.ObjectId]("2"))
        when(mockDbObject.getAs[String](NAME)).thenReturn(Option(testName))
        when(mockDbObject.getAs[String](TICKER)).thenReturn(Option(testTicker))
        when(mockDbObject.getAs[Integer](SECTORID)).thenReturn(Option[Integer](testSectorId))
        when(mockDbObject.getAs[String](SOURCE)).thenReturn(Option(testSource))
        
        val testSector = new Sector(null, testName, testTicker, testSectorId, testSource) 
        
        val sector = SectorConverter.convertFromMongoObject(mockDbObject)
        sector.ticker should be (testTicker)
        sector.sectorId should be (testSectorId)
        sector.name should be (testName)
        sector.source should be (testSource)
        
        
           
      }
    }
  }
	**/
  

}