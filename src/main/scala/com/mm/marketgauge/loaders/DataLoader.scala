package com.mm.marketgauge.loaders

import com.mm.marketgauge.persistence.{PersistenceServiceComponent}
import com.mm.marketgauge.dao.MongoDatabase
import com.mm.marketgauge.service._
import com.typesafe.config.Config


/**
 * Trait for a generic DataLoader.
 * This dataLoader will upload data for sectors and companies, as well as share prices 
 * Identify role of PersisteceService. should be embedded into SectorService and perhaps
 * renamed to PersistenceManager instead
 * To replace using DependencyInjection
 */
trait DataLoader  {
   self:PersistenceServiceComponent =>
  
    type DataService
  
  private [loaders]val notifier:Notifier
  
  /**
   * load data into database. 
   * @return the amount of row inserted
   */
  def load:Int
  
  /**
   * Notify subscribers of outcome of this load
   * @param subject: string representing subject
   * @param message: string representing the message
   */
  def notify(subject:String, message:String) = notifier.notify(subject, message)
  
  
}

object DataLoader {
  
  def getLoader(loaderName:String, config:Config):DataLoader =  {
    
    trait ConfigLoader  extends PersistenceServiceComponent with DataDownloaderComponent {
      // try to remove factories if you can
      // take out factories. Mix in traits instead
      val notifier = Notifier.defaultNotifier(config)         
      
    }
    
    loaderName match {
          case "sectors" => new SectorLoader with SectorServiceComponent with ConfigLoader 
          
          case "companies" => new CompaniesLoader with CompanyServiceComponent with ConfigLoader 
          
          case "prices" => new SharePriceLoader with SharePriceServiceComponent  with ConfigLoader 
          
          case "shares" => new CustomSharesLoader with ConfigLoader  
          
          case "tester" =>  new CustomSharesTester with ConfigLoader with SharePriceServiceComponent 
    }
          
  
  }
  
}