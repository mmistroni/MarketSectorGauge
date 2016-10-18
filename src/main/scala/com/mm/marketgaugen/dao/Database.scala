package com.mm.marketgaugen.dao

abstract class Database {
  val username:String
  val password:String
  val uri     :String
  val databaseName : String
  
}


object Database extends com.mm.marketgauge.util.LogHelper{
  
  def getDatabase(user:String, pwd:String, 
                  dbUri:String, dbName:String) = {
      logger.info(s"dbUri:$dbUri")
      new MongoDatabase {
                          val username = user
                          val password = pwd
                          val uri = dbUri
                          val databaseName = dbName
      }
  }
}

