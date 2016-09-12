package com.mm.marketgauge.converters

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures._
import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.entities.SectorProperties._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.mongodb.casbah.Imports._

@RunWith(classOf[JUnitRunner])
class SectorConverterSpec extends FreeSpec with Matchers {
                      
  "The SectorConverter" - {
    "when calling convertToMongoObject with a Sector object " - {
      "should return a DbObject with all properties set" in {
        val testName = "sectorName"
        val testTicker ="sectorTicker"
        val sectorId = -1
        val source = "sectorSource"
        val testSector = new Sector(null, testName, testTicker, sectorId, source) 
        
        val dbObject = SectorConverter.convertToMongoObject(testSector)
        dbObject.containsField(NAME) should be (true)
        dbObject.containsField(TICKER) should be (true)
        dbObject.containsField(SECTORID) should be (true)
        dbObject.containsField(SOURCE) should be (true)
        
        dbObject.get(NAME).toString should be (testName)
        dbObject.get(TICKER).toString should be (testTicker)
        dbObject.get(SECTORID).asInstanceOf[Int] should be (sectorId)
        dbObject.get(SOURCE) should be (source)
        
        
        
           
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
        when(mockDbObject.getAs[ObjectId](ID)).thenReturn(Option[com.mongodb.casbah.Imports.ObjectId](null))
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