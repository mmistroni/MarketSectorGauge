package com.mm.marketgauge.persistence.dynamodb

import com.mm.marketgauge.entities.SharePrice
import com.gu.scanamo.{Table, Scanamo, DynamoFormat}
import com.gu.scanamo.syntax._
import com.mm.marketgauge.entities.SharePriceProperties._
import scala.util.{Either, Left, Right}


trait SharePriceRepository extends BaseDynamoRepository[SharePrice] {
  
  
  override val tableName = "share_prices"
  
  override def getAll = {
    val result = Scanamo.scan[SharePrice](client)(tableName)
    
    result.foldLeft(Seq[SharePrice]())((accumulator, item) => {
            item match {
              case Right(price) => price +: accumulator
              case Left(error) => accumulator
            }
         })
    
  }
  
  
  def insert(all:Seq[SharePrice]) = {
    Scanamo.putAll[SharePrice](client)(tableName)(all.toSet).size
  }
  
  def delete(prices:Seq[SharePrice]) = {
    import com.gu.scanamo.query._
    val uniqueKeys = prices.map(i => (i.ticker, i.asOfDate) ).toSet
    Scanamo.deleteAll(client)(tableName)(UniqueKeys(MultipleKeyList( ('ticker, 'asOfDate),
                                          uniqueKeys ))) 
    0
  }
  
  
}