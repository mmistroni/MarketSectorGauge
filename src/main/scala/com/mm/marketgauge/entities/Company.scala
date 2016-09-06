package com.mm.marketgauge.entities
import org.bson.types.ObjectId
import scala.util.control.Exception.allCatch
import com.mm.marketgauge.util.Utilities.getDouble

/**
 * Abstraction of company.
 * Data for companies is downloaded from yahoo
 */
case class Company (_id:ObjectId, name :String ,priceChange: Double, marketCap:Double, 
                    priceToEarnings :Double, roe:Double, divYield: Double, 
                    ltDToEq:Double, priceToBook : Double, netProfMgn: Double, 
                    priceToCashFlow:Double, sectorId:Int)

object Company {
  
  def fromListOfString(inputList:List[String]) = {
    
    if (inputList.size > 2) {
      Company( null, inputList(0),
            getDouble(inputList(1)),
            getDouble(inputList(2).replace("B", "").replace("M","")),
            getDouble(inputList(3)),
            getDouble(inputList(4)),
            getDouble(inputList(5)),
            getDouble(inputList(6)),
            getDouble(inputList(7)),
            getDouble(inputList(8)),
            getDouble(inputList(9)),
            inputList(10).toInt)
    } else
      null
            
  }
  
  
}
                      
object CompanyProperties {
  val ID = "_id"
  val NAME = "name"
  val PRICECHANGE = "priceChange"
  val MARKETCAP = "marketCap"
  val PRICETOEARNINGS = "priceToEarnings"
  val ROE = "roe"
  val DIVYIELD = "divYield"
  val LTDTOEQ = "ltdToEq"
  val PRICETOBOOK = "priceToBook"
  val NETPROFMGN = "netProfMgn"
  val PRICETOCASHFLOW = "priceToCashFlow"
  val SECTORID = "sectorId"
}


case class CompanyRepo (_id:ObjectId, ticker:String, name:String, 
                      lastSale:Double, marketCap:String, 
                      ipoYear:String, sector:String, industry:String)

  

object CompanyRepo {
  
  def fromListOfString(inputList:List[String]) = {
    CompanyRepo(null, inputList(0), inputList(1),
                getDouble(inputList(2).replace("B", "")), 
                inputList(3),
                inputList(4).replace("B", ""), inputList(5), inputList(6))
  }

}

             

object CompanyRepoProperties {
  val ID = "_id"
  val NAME = "name"
  val TICKER = "ticker"
  val LASTSALE = "lastSale"
  val MARKETCAP = "marketCap"
  val IPOYEAR = "ipoYear"
  val SECTOR = "sector"
  val INDUSTRY = "industry"
}                







