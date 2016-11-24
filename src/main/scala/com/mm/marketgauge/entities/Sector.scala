package com.mm.marketgauge.entities
import org.bson.types.ObjectId


/**
 * Abstraction of a Sector
 * Each sector comprises
 * - name
 * - ticker
 * - sectorId, which is a number which is needed in order to download companies
 *             for the sector
 * - source: source of the data. Most of the sectors can be fetched via yahoo finance, but few others
 *           are available via google service or bloomberg
 */
case class Sector (name:String, ticker:String, sectorId:Int, source:String)


object SectorProperties {
  val ID = "_id"
  val NAME = "name"
  val TICKER = "ticker"
  val SECTORID = "sectorId"
  val SOURCE = "source"
  
}                
