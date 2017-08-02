package com.mm.marketgauge.dao

import com.mongodb.casbah.{MongoClient, MongoClientURI, WriteConcern}
import com.mongodb.casbah.Imports._

trait BaseDao {
  
  type T
  
  val database:MongoDatabase
  
  def insert(values: Seq[T]):Int
  
  def getAll:Seq[T] = null
  
  def findByExample(example:T):Seq[T] = null
  
    
}

