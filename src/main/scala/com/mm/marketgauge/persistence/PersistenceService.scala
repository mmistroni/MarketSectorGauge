package com.mm.marketgauge.persistence

import com.mm.marketgauge.entities.Sector
import com.mm.marketgauge.entities.Company
import com.mm.marketgauge.entities.SharePrice


/**
 * Global Persistence Service for the application
 */
trait PersistenceService {
  
  /** 
   *  component holding the connection to specific db
   */
  val dbAccess:DbComponent
  
  def getAllSectors:Seq[Sector] = { null }
  
  def getCompany(companyTicker:String):Company
  
  def getSector(sectorId:Int):Sector
  
  def storeSector(data:Sector):Sector
  
  def storeCompany(data:Sector):Sector
  
  def storePrice(data:SharePrice):SharePrice
  
  
}