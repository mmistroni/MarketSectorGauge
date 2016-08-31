package com.mm.marketgaugen.dao

import com.mm.marketgauge.entities.{Company, CompanyRepo}
import com.mongodb.casbah.{MongoClient, MongoClientURI, WriteConcern}
import com.mm.marketgauge.converters.{CompanyConverter, CompanyRepoConverter}
import com.mongodb.casbah.Imports._

/**
 * User: talg
 */

trait CompanyDao {
  /**
   * Mongo URI string [[http://docs.mongodb.org/manual/reference/connection-string/]]
   */
  private val uri = """mongodb://localhost:27017/"""
  val db = MongoClient(MongoClientURI(uri))( """test""")
  val companyCollection = db("companies")
  val repoCollection = db("companiesrepo")
  
  
  def insertCompanies(companies: Company*):Int = {
    val builder = companyCollection.initializeOrderedBulkOperation
    companies.foreach(s => builder.insert(CompanyConverter.convertToMongoObject(s)))
    builder.execute().insertedCount
    
  }

  def insertCompanyRepos(companyRepos: CompanyRepo*):Int = {
    val builder = repoCollection.initializeOrderedBulkOperation
    companyRepos.foreach(s => builder.insert(CompanyRepoConverter.convertToMongoObject(s)))
    builder.execute().insertedCount
    
  }
  
  
  
  
  
  
}