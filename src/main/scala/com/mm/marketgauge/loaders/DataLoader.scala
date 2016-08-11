package com.mm.marketgauge.loaders

import com.mm.marketgauge.persistence.PersistenceService
import com.mm.marketgauge.service.DataDownloader
/**
 * Trait for a generic DataLoader.
 * This dataLoader will upload data for sectors and companies, as well as share prices 
 */
abstract class DataLoader {
  val downloader:DataDownloader
  val persistenceService:PersistenceService
  private val allSectorsUrl = "http://biz.yahoo.com/ic/ind_index_alpha.html"
  private val companyUrl = "http://biz.yahoo.com/p/csv/<sectorid>conameu.csv"
  
  def uploadSectors:Unit
    // we need to get all sectors and for each sector get companies and store them
  def uploadCompanies:Unit
  
  def uploadPrices:Unit
  
}