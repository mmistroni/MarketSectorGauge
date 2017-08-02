package com.mm.marketgauge.dao

import com.mm.marketgauge.entities.{Company, CompanyRepo}
import com.mongodb.casbah.{MongoClient, MongoClientURI, WriteConcern}
import com.mongodb.casbah.Imports._
import com.mm.marketgauge.util.LogHelper

/**
 * User: talg
 */

trait CompanyRepoDao extends BaseDao with LogHelper {
  /**
   * Mongo URI string [[http://docs.mongodb.org/manual/reference/connection-string/]]
   */
  lazy val repoCollection = database.client("companiesrepo")
  
  type T = CompanyRepo
  
  def insert(repos:Seq[CompanyRepo]) = insertCompanyRepos(repos:_*)
  
  def insertCompanyRepos(companyRepos: CompanyRepo*):Int = {
    repoCollection.findOne() match {
      case Some(coll) => {  
              logger.info("...There's data in repo db. updating one by one...") 
              insertRepoIndividually(companyRepos:_*)
            }
      case None => {
          logger.info("Bulk RepoInsert....")
          bulkInsertRepo(companyRepos:_*)
      }
    }
  }    
    
  private def bulkInsertRepo(companyRepos:CompanyRepo*):Int = {  
    val builder = repoCollection.initializeOrderedBulkOperation
    companyRepos.foreach(s => builder.insert(CompanyRepoConverter.convertToMongoObject(s)))
    builder.execute().insertedCount
    
  }
  
  private def insertRepoIndividually(companyRepos:CompanyRepo*):Int = {
    var updates = 0
    for (companyRepo <- companyRepos) {
      val q = MongoDBObject("ticker" -> companyRepo.ticker)
      
      logger.info(s"Updating ${companyRepo.name}")
      
      val update = MongoDBObject(
          
              "$set" -> MongoDBObject("name" -> companyRepo.name,
                                      "marketCap" -> companyRepo.marketCap,
                                      "sector" -> companyRepo.sector,
                                      "ticker" -> companyRepo.ticker,
                                      "ipoYear" -> companyRepo.ipoYear,
                                      "industry" -> companyRepo.industry)
                )
      repoCollection.update(q, update, true)
      updates +=1
   }
   updates
  }
  
  
  
  
  
}