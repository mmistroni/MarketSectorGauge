package com.mm.marketgaugen.dao

abstract class Database {
  val username:String
  val password:String
  val uri     :String
  val databaseName : String
  
}


object Database {
  
  def getDatabase(user:String, pwd:String, 
                  dbUri:String, dbName:String) = new MongoDatabase {
                                                          val username = user
                                                          val password = pwd
                                                          val uri = dbUri
                                                          val databaseName = dbName
  }
}

