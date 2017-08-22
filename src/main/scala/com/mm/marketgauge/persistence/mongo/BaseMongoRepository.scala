package com.mm.marketgauge.persistence.mongo
import com.mm.marketgauge.util.AppConfig
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import com.mm.marketgauge.persistence.{BaseRepository, BaseRepositoryQuery}

abstract class BaseMongoRepository[DBObject,E] 
    extends BaseRepository[DBObject,E] {
  
  val collectionName:String
  lazy val client = MongoClient(MongoClientURI(AppConfig.DbConfig.uri))(AppConfig.DbConfig.dbName)
  
  
  def mongoFail(field:String) = throw new Exception(s"Unable to access object for :$field")
  
  def getAll:Seq[E] = {
    client(collectionName).toList.map(item => convertFromMongoObject(item))
  }
  
  def convertFromMongoObject(mongoObject:com.mongodb.casbah.commons.TypeImports.DBObject):E
  
  def convertToMongoObject(entity:E):com.mongodb.casbah.commons.TypeImports.DBObject
  
  def insert(all:Seq[E]) = insertBulk(all)
  
  def delete(all:Seq[E]) = 0
  
  private def insertBulk(sectors: Seq[E]):Int = {
    client(collectionName).findOne() match {
      case Some(coll) => {
              insertIndividually(sectors)
            }
      case None => {
          bulkInsert(sectors)
      }
    }
  }

  private[persistence] def insertIndividually(sectors:Seq[E]):Int 
  
  private[persistence] def bulkInsert(sectors:Seq[E]):Int = {
    val builder = client(collectionName).initializeOrderedBulkOperation
    sectors.foreach(s => builder.insert(convertToMongoObject(s)))
    builder.execute().insertedCount
    
  }
  
 }