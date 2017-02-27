package com.mm.marketgauge.service

object LoaderExecutor extends App with com.mm.marketgauge.util.LogHelper{
  
  val loadersMap = Map("sectors"->SectorLoader, 
                       "companies" -> CompaniesLoader,
                       "prices" -> SharePriceLoader,
                       "shares" -> CustomSharesLoader,
                       "tester" -> CustomSharesTester)
  
  
  val loaderName =  args(0)
  
  logger.info(s"loader to launch is:$loaderName")
  
  loadersMap.get(loaderName).get.main(null)                     
                       
                       
}