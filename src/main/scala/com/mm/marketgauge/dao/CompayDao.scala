package com.mm.marketgauge.dao

import com.mm.marketgauge.entities.{Company, CompanyRepo}
import com.mongodb.casbah.{MongoClient, MongoClientURI, WriteConcern}
import com.mongodb.casbah.Imports._
import com.mm.marketgauge.util.LogHelper

/**
 * User: talg
 */

trait CompanyDao extends BaseDao with LogHelper {
  /**
   * Mongo URI string [[http://docs.mongodb.org/manual/reference/connection-string/]]
   */
  lazy val companyCollection = database.client("companies")
  lazy val repoCollection = database.client("companiesrepo")
  
  
  def insertCompanies(companies:Company*):Int = {
    companyCollection.findOne() match {
      case Some(coll) => {
              logger.info("...There's data in db. updating one by one...") 
              insertIndividually(companies:_*)
            }
      case None => {
          logger.info("Bulk Insert....")
          bulkInsert(companies:_*)
      }
    }
  }
  
  private def bulkInsert(companies: Company*):Int = {
    logger.info("Dao. Inserting:" + companies.length)
    val builder = companyCollection.initializeOrderedBulkOperation
    companies.foreach(s => builder.insert(CompanyConverter.convertToMongoObject(s)))
    builder.execute().insertedCount
  }

  
  private def insertIndividually(companies:Company*):Int = {
    var updates = 0
    for (company <- companies) {
      val q = MongoDBObject("name" -> company.name)
      
      logger.info(s"Updating ${company.name}")
      
      val update = MongoDBObject(
              "$set" -> MongoDBObject("name" -> company.name,
                                      "marketCap" -> company.marketCap,
                                      "sectorId" -> company.sectorId,
                                      "priceToEarning" -> company.priceToEarnings,
                                      "roe" -> company.roe,
                                      "divYield" -> company.divYield,
                                      "ltdToEq" -> company.ltDToEq,
                                      "priceToBook" -> company.priceToBook,
                                      "priceToCashFlow" -> company.priceToCashFlow)
                )
      companyCollection.update(q, update, true)
      updates +=1
   }
   updates
  }
  
  
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