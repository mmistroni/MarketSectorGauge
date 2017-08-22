package com.mm.marketgauge.persistence

/**
 * This trait should define the Database-specific queries which
 * are non trivial. for example when using some sort of criterias
 	 For example, for mongo we should handle something like this
 	 
 	 lazy val collection = database.client("sectors")
  type T = Sector

  
  def insert(all:Seq[Sector]) = insertBulk(all)
  
  def insertBulk(sectors: Seq[Sector]):Int = {
    collection.findOne() match {
      case Some(coll) => {
              println("Individual inserrt" + sectors.mkString(","))
              insertIndividually(sectors)
            }
      case None => {
          bulkInsert(sectors)
      }
    }
 	 
 * While for example for DynamoDb we need to handle something like
 * this
 * 
 * Scanamo.put(client)("farmer")(Farmer("McDonald", 159L))
  //Scanamo.exec(client)(table.put(Farmer("McDonald", 156L, Farm(List("sheep", "cow")))))
  // res2: com.amazonaws.services.dynamodbv2.model.PutItemResult = {}
  println(Scanamo.get[Farmer](client)("farmer")('name -> "McDonald"))
  //println(Scanamo.exec(client)(table.get('name -> "McDonald")))
 * 
 * 
 *  
 * 
 * */
trait BaseRepositoryQuery[T,E] {
 
  // so maybe for the moment we dont need to use this
 
   
  
}