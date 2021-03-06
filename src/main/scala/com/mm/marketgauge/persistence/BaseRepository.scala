package com.mm.marketgauge.persistence
import com.mm.marketgauge.entities._

/**
 * Generic repository trait. 
 * we need to handle for now two types of query:
 * - MongoDb ==>
 *      client = MongoClient(MongoClientURI(uri))(databaseName)
 *      lazy val collection = database.client("sectors")
 * - DynamoDb via scanamo ==>
 *     val client = LocalDynamoDB.client("http://localhost:9999")
 *     Scanamo.put(client)("farmer")(Farmer("McDonald", 159L))
       Scanamo.get[Farmer](client)("farmer")('name -> "McDonald")
       
 So Ultimately we need 1 repository per entity - in our case 4(Sector,Company, CompanyRepo, SharePrice)
 Now the open question is how to handle conversion betwen entity and persisted object , and where
 For example, DyamoDb persisst directy case classes, while MongoDb requires entities to have an id of Mongo's ObjectId.
 ORM such as Hibernate would expect some sort of Entity....so ideally the repository should handle two types:
 * 
 * */

import com.mm.marketgauge.util.AppConfig
trait BaseRepository[T] extends com.mm.marketgauge.util.LogHelper {
  
  /**
   * Inserts multiple item in the repository
   * @param items a Seq of Item
   * @return: the number of entities persisted
   */
  def insert(items: Seq[T]):Int 
 
  /**
   * delete multiple items
   * @param:items a sequence of items
   * @return: an integer representing the number of deletions
   */
  def delete(items: Seq[T]):Int
 
  /**
   * Return a sequence of item for the specified query
   */
  def getByQuery(query:BaseRepositoryQuery[T]):Seq[T] = Nil
  
  /**
   * Return all the items in the query
   */
  def getAll:Seq[T] 
 
  
}



trait BaseSectorRepository extends BaseRepository[Sector] {
  type Entity = com.mm.marketgauge.entities.Sector
}
  
trait BaseCompanyRepository extends BaseRepository[Company] {
  type Entity = com.mm.marketgauge.entities.Company
}

trait BaseCompanyRepoRepository extends BaseRepository[CompanyRepo] {
  type Entity = com.mm.marketgauge.entities.CompanyRepo
}

trait BaseSharePriceRepository extends BaseRepository[SharePrice] {
  type Entity = com.mm.marketgauge.entities.SharePrice
}


 

