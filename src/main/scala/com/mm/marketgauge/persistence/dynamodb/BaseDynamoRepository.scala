package com.mm.marketgauge.persistence.dynamodb

import com.mm.marketgauge.util.AppConfig
import com.mm.marketgauge.persistence.{BaseRepository, BaseRepositoryQuery}
import com.gu.scanamo.{Table, Scanamo, DynamoFormat}
import com.gu.scanamo.syntax._
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType._
import com.amazonaws.services.dynamodbv2._
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.regions.Regions


abstract class BaseDynamoRepository[T] extends BaseRepository[T] {
  
  val tableName:String
  
  lazy val client = 
    AmazonDynamoDBClientBuilder.standard()
                  .withRegion(Regions.valueOf(AppConfig.AwsConfig.region))
                  .build()
  
  
 }