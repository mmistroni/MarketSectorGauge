package com.mm.marketgauge.entities

/**
 * Abstraction of company.
 * Data for companies is downloaded from yahoo
 */
case class Company (name :String ,priceChange: Double, marketCap:Double, 
                    priceToEarnings :Double, roe:Double, divYield: Double, 
                    ltDToEq:Double, priceToBook : Double, netProfMgn: Double, 
                    priceToCashFlow:Double, sectorId:Int)

case class CompanyRepo (ticker:String, name:String, 
                      lastSale:Double, marketCap:String, 
                      ipoYear:String, sector:String, industry:String)

  
object Company {
  
  def fromListOfString(inputList:List[String]) = {
    Company( inputList(0),
            inputList(1).toDouble,
            inputList(2).replace("B", "").toDouble,
            inputList(3).toDouble,
            inputList(4).toDouble,
            inputList(5).toDouble,
            inputList(6).toDouble,
            inputList(7).toDouble,
            inputList(8).toDouble,
            inputList(9).toDouble,
            inputList(10).toInt)
            
  }
}
                      
                    
object CompanyRepo {
  
  def fromListOfString(inputList:List[String]) = {
    CompanyRepo(inputList(0), inputList(1),
                inputList(2).replace("B", "").toDouble, 
                inputList(3),
                inputList(4).replace("B", ""), inputList(5), inputList(6))
  }
                      
                                                                
}