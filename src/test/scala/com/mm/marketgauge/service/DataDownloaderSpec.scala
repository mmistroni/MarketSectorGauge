 package com.mm.marketgauge.service

import org.scalatest.BeforeAndAfterAll
import org.scalatest.FreeSpec
import org.scalatest.Matchers
import org.mockito.{ Mockito, Matchers => MockitoMatchers }
import org.scalatest.concurrent.ScalaFutures._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner



@RunWith(classOf[JUnitRunner])
class DataDownloaderSpec extends FreeSpec with Matchers {
 
  "The DataDownloader" - {
    "when calling downloadFromURL with a String representing an URL" - {
      "should return an Iterator of Strings" in {
        
        val testUrl = "http://myurl"
        val stringIterator = List("foo").iterator
        val dataDownloader = DataDownloader.getDataDownloader()  
        val dataDownloaderSpy = Mockito.spy(dataDownloader)

        val testBufferedSource = Mockito.mock(classOf[scala.io.BufferedSource])
        Mockito.when(testBufferedSource.getLines()).thenReturn(stringIterator)
        
        Mockito.doReturn(testBufferedSource).when(dataDownloaderSpy)._getFromUrl(testUrl)
        
        val downloaderIterator = dataDownloaderSpy.downloadFromURL(testUrl)
        
        downloaderIterator shouldEqual(stringIterator)
        Mockito.verify(dataDownloaderSpy)._getFromUrl(testUrl)
        
      }
    }
  }
  
  
  "The DataDownloader" - {
    "when calling downloadCSV with a String representing an URL" - {
      "should return an Iterator of Strings" in {
        
        val testUrl = "http://myurl"
        val testCsvData = List(List("testdata"))
        val dataDownloader = DataDownloader.getDataDownloader()  
        val dataDownloaderSpy = Mockito.spy(dataDownloader)
        
        Mockito.doReturn(testCsvData).when(dataDownloaderSpy)._getFromCSVReader(testUrl)
        
        val downloadedData = dataDownloaderSpy.downloadCSV(testUrl)
        
        downloadedData shouldEqual(testCsvData)
        Mockito.verify(dataDownloaderSpy)._getFromCSVReader(testUrl)
        
      }
    }
  }
  
  
  
}