package com.mm.marketgauge.service

object LoaderExecutor extends App{
  
  val loadersMap = Map("sectors"->SectorLoader, 
                       "companies" -> CompaniesLoader,
                       "prices" -> SharePriceLoader)
  
  
  val loaderName =  args(0)
  
  println(s"loader to launch is:$loaderName")
  
  loadersMap.get(loaderName).get.main(null)                     
                       
                       
}