package com.mm.marketgauge.persistence

import slick.driver.H2Driver.api._
import com.mm.marketgauge.entities.Company


class Companies(tag: Tag) extends Table[Company](tag, "") {
  
  def name = column[String]("name", O.Length(20))
  def priceChange = column[Double]("priceChange")
  def marketCap = column[Double]("marketCap")
  def priceToEarnings = column[Double]("priceToEarnings")
  def roe = column[Double]("roe")
  def divYield = column[Double]("divYield")
  def ltdToEq = column[Double]("ltdToEq")
  def priceToBook = column[Double]("priceToBook")
  def netProfMgn = column[Double]("netProfMgn")
  def priceToCashFlow = column[Double]("priceToCashFlow")
  def sectorId = column[Int]("sectorId")
  
  def * = (name, priceChange, marketCap, priceToEarnings,
           roe, divYield, ltdToEq, priceToBook,
           netProfMgn, priceToCashFlow, sectorId) <>
    ((Company.apply _).tupled, Company.unapply _)
}

object Companies {
  val companies = TableQuery[Companies]
}
