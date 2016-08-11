package com.mm.marketgauge.persistence

import slick.driver.H2Driver.api._
import com.mm.marketgauge.entities.Sector

class Sectors(tag: Tag) extends Table[Sector](tag, "") {
  
  def name = column[String]("name", O.Length(20))
  def ticker = column[String]("ticker", O.Length(10))
  def sectorId = column[Int]("sectorId")
  def source = column[String]("source", O.Length(10))
  
  def * = (name, ticker, sectorId, source) <>
    ((Sector.apply _).tupled, Sector.unapply _)
}

object Sectors {
  val sectors = TableQuery[Sectors]
}
