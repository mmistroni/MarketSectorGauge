package com.mm.marketgauge.service
import scala.io.Source
import com.github.tototoshi.csv._

/**
 * Trait for a DataDowloader which fetches data
 * from a supplied URL
 * These are the URL that we need
 * 
 * # view all companies for a sector. the one below is Financials
	#https://biz.yahoo.com/p/csv/431conameu.csv

  # list of all sectors from yahoo
  #http://biz.yahoo.com/ic/ind_index_alpha.html

  # historical prices
  #'http://ichart.finance.yahoo.com/table.csv?s=%(ticker)s&a=%(fromMonth)s&b=%(fromDay)s&c=%(fromYear)s&d=%(toMonth)s&e=%(toDay)s&f=%(toYear)s&g=d&ignore=.csv' 
 * 
 * 
 * 
 */
trait DataDownloader  {
  
  def downloadFromURL(url:String):Iterator[String] = {
    _getFromUrl(url).getLines()
  }
  
  def downloadCSV(url:String):List[List[String]] = {
    CSVReader.open(_getFromUrl(url)).all()
  }
  
  
  def _getFromUrl(url:String) = Source.fromURL(url)
    
}