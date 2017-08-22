package com.mm.marketgauge.util
import com.gu.scanamo._
import com.gu.scanamo.syntax._
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType._
import com.amazonaws.services.dynamodbv2._
import amazon.util.LocalDynamoDB


case class Farm(animals: List[String])
case class Farmer(name: String, age: Long)


object ScanamoApp extends App {

  
  val client = LocalDynamoDB.client("http://localhost:9999")
  //val farmersTableResult = LocalDynamoDB.createTable(client)("farmer")('name -> S)
  //we can simply put and get items from Dynamo, without boilerplate or reflection
  
  //val table = Table[Farmer]("farmer")
  // table: com.gu.scanamo.Table[Farmer] = Table(farmer)

  Scanamo.put(client)("farmer")(Farmer("McDonald", 159L))
  //Scanamo.exec(client)(table.put(Farmer("McDonald", 156L, Farm(List("sheep", "cow")))))
  // res2: com.amazonaws.services.dynamodbv2.model.PutItemResult = {}
  println(Scanamo.get[Farmer](client)("farmer")('name -> "McDonald"))
  //println(Scanamo.exec(client)(table.get('name -> "McDonald")))

}