package com.mm.marketgauge.loaders

import com.mm.marketgauge.persistence.PersistenceService
import com.mm.marketgauge.service.DataDownloader
/**
 * Trait for a generic DataLoader.
 * This dataLoader will upload data for sectors and companies, as well as share prices 
 */
trait DataLoader {
  
  private [loaders] val downloader:DataDownloader = new DataDownloader {}
  
  def load:Int
  
}