package com.mm.marketgauge.loaders

import com.typesafe.config.ConfigFactory
object LoaderExecutor extends App with com.mm.marketgauge.util.LogHelper{
  
  // Entry point. 
  // get the conf here and pass it on. this is the entry point
  
  val config = ConfigFactory.load()
  val loaderName = args(0)
  logger.info(s"loader to launch is:$loaderName")
  
  val keys = config.entrySet()
  val username = config.getString("db.username")
  val password = config.getString("db.password")
  val uri = config.getString("db.uri")
  val databaseName = config.getString("db.name")
  println(s"Username:$username|Password:$password|Uri:$uri|dbName:$databaseName")    
  
  DataLoader.getLoader(loaderName, config).load
                       
                       
}