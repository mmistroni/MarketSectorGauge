package com.mm.marketgauge.dao

import org.junit.runner.RunWith
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.scalatest.junit.JUnitRunner

import com.mm.marketgauge.entities.SharePrice
import com.mm.marketgauge.entities.SharePriceProperties._

@RunWith(classOf[JUnitRunner])
class SharePriceConverterSpec extends FreeSpec with Matchers {
                      
  "The SharePriceConverter" - {
    "when calling convertToMongoObject with a SharePrice object " - {
      "should return a DbObject with all properties set" in {
        
        val testSharePrice = new SharePrice("ticker", 1.0, "11/8/2016", 
            2.0,3.0,4.0,"11/11/2015",5.0,6.0, "1.0B")
            
        
        
        val dbObject = SharePriceConverter.convertToMongoObject(testSharePrice)
        dbObject.containsField(TICKER) should be (true)
        dbObject.containsField(ASOFDATE) should be (true)
        dbObject.containsField(PRICE) should be (true)
        dbObject.containsField(CURRENTEPS) should be (true)
        dbObject.containsField(FORWARDEPS) should be (true)
        dbObject.containsField(MOVINGAVG) should be (true)
        dbObject.containsField(EXDIVDATE) should be (true)
        dbObject.containsField(PEG) should be (true)
        dbObject.containsField(SHORTRATIO) should be (true)
        dbObject.containsField(CREATED_TIME) should be (true)
        dbObject.containsField(MARKETCAP) should be (true)
        
        dbObject.get(TICKER).toString shouldEqual (testSharePrice.ticker)
        dbObject.get(ASOFDATE).toString shouldEqual(testSharePrice.asOfDate)
        dbObject.get(PRICE).asInstanceOf[Double] shouldEqual (testSharePrice.price)
        dbObject.get(CURRENTEPS).asInstanceOf[Double] shouldEqual (testSharePrice.currentEps)
        dbObject.get(FORWARDEPS).asInstanceOf[Double] shouldEqual(testSharePrice.forwardEps)
        dbObject.get(MOVINGAVG).asInstanceOf[Double] shouldEqual(testSharePrice.movingAverage)
        dbObject.get(EXDIVDATE).toString shouldEqual (testSharePrice.exDivDate)
        dbObject.get(PEG).asInstanceOf[Double] shouldEqual(testSharePrice.peg)
        dbObject.get(SHORTRATIO).asInstanceOf[Double] shouldEqual(testSharePrice.shortRatio)
        dbObject.get(CREATED_TIME) shouldEqual (new java.text.SimpleDateFormat("MM/dd/yyyy").parse(testSharePrice.asOfDate))
        dbObject.get(MARKETCAP).toString shouldEqual (testSharePrice.marketCap)
        
        
        
           
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