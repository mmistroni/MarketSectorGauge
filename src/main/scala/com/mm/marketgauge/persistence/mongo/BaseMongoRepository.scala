package com.mm.marketgauge.persistence.mongo
import com.mm.marketgauge.util.AppConfig
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import com.mm.marketgauge.persistence.{BaseRepository, BaseRepositoryQuery}
import com.mongodb.casbah.commons.TypeImports.DBObject
import com.mongodb.casbah.Imports._


abstract class BaseMongoRepository[T]
    extends BaseRepository[T] {
  
  type DatabaseObject = DBObject
  
  val collectionName:String
  lazy val client = MongoClient(MongoClientURI(AppConfig.DbConfig.uri))(AppConfig.DbConfig.dbName)
  
  
  def mongoFail(field:String) = throw new Exception(s"Unable to access object for :$field")
  
  def getAll:Seq[T] = {
    client(collectionName).toList.map(item => convertFromMongoObject(item))
  }
  
  def convertFromMongoObject(mongoObject:com.mongodb.casbah.commons.TypeImports.DBObject):T
  
  def convertToMongoObject(entity:T):com.mongodb.casbah.commons.TypeImports.DBObject
  
  def insert(all:Seq[T]) = insertBulk(all)
  
  def delete(all:Seq[T]) = 0
  
  private def insertBulk(sectors: Seq[T]):Int = {
    client(collectionName).findOne() match {
      case Some(coll) => {
              insertIndividually(sectors)
            }
      case None => {
          bulkInsert(sectors)
      }
    }
  }

  private[persistence] def insertIndividually(sectors:Seq[T]):Int 
  
  private[persistence] def bulkInsert(sectors:Seq[T]):Int = {
    val builder = client(collectionName).initializeOrderedBulkOperation
    sectors.foreach(s => builder.insert(convertToMongoObject(s)))
    builder.execute().insertedCount
    
  }
  
 }