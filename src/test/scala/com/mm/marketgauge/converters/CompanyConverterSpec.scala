package com.mm.marketgauge.converters

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures._
import com.mm.marketgauge.entities.Company
import com.mm.marketgauge.entities.CompanyProperties._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mongodb.casbah.Imports._

@RunWith(classOf[JUnitRunner])
class CompanyConverterSpec extends FreeSpec with Matchers {
               
  def _buildCompany = {
    Company(null, "name" , -1.0, 1000.0, 
                    -4.1, -5.2, -6.1, 
                    -7.1, -3.0, -9.1, 
                    -4.0, -99)
  }
  
  
  "The CompanyConverter" - {
    "when calling convertToMongoObject with a Company object " - {
      "should return a DbObject with all properties set" in {
        
        val testCompany = _buildCompany 
        
          
        val dbObject = CompanyConverter.convertToMongoObject(testCompany)
        
        println(dbObject)
        dbObject.containsField(NAME) should be (true)
        dbObject.containsField(PRICECHANGE) should be (true)
        dbObject.containsField(MARKETCAP) should be (true)
        dbObject.containsField(PRICETOEARNINGS) should be (true)
        dbObject.containsField(ROE) should be (true)
        dbObject.containsField(DIVYIELD) should be (true)
        dbObject.containsField(LTDTOEQ) should be (true)
        dbObject.containsField(PRICETOBOOK) should be (true)
        dbObject.containsField(NETPROFMGN) should be (true)
        dbObject.containsField(PRICETOCASHFLOW) should be (true)
        dbObject.containsField(SECTORID) should be (true)
        
        
        dbObject.get(NAME).toString should be (testCompany.name)
        dbObject.get(PRICECHANGE).asInstanceOf[Double] should be (testCompany.priceChange)
        dbObject.get(MARKETCAP).asInstanceOf[Double] should be (testCompany.marketCap)
        dbObject.get(PRICETOEARNINGS).asInstanceOf[Double] should be (testCompany.priceToEarnings)
        dbObject.get(ROE).asInstanceOf[Double] should be (testCompany.roe)
        dbObject.get(DIVYIELD).asInstanceOf[Double] should be (testCompany.divYield)
        dbObject.get(LTDTOEQ).asInstanceOf[Double] should be (testCompany.ltDToEq)
        dbObject.get(PRICETOBOOK).asInstanceOf[Double] should be (testCompany.priceToBook)
        dbObject.get(NETPROFMGN).asInstanceOf[Double] should be (testCompany.netProfMgn)
        dbObject.get(PRICETOCASHFLOW).asInstanceOf[Double] should be (testCompany.priceToCashFlow)
        dbObject.get(SECTORID).asInstanceOf[Int] should be (testCompany.sectorId)
        
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