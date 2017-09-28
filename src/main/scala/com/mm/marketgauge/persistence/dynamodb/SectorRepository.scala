package com.mm.marketgauge.persistence.dynamodb

import com.mm.marketgauge.entities.Sector
import com.gu.scanamo.{Table, Scanamo, DynamoFormat}
import com.gu.scanamo.syntax._
import com.mm.marketgauge.entities.SectorProperties._
import scala.util.{Either, Left, Right}


trait SectorRepository extends BaseDynamoRepository[Sector] {
  
  
  override val tableName = "sectors_test"
  
  override def getAll = {
    val result = Scanamo.scan[Sector](client)(tableName)
    
    result.foldLeft(Seq[Sector]())((accumulator, item) => {
            item match {
              case Right(sector) => sector +: accumulator
              case Left(error) => accumulator
            }
         })
    
  }
  
  
  def insert(all:Seq[Sector]) = {
    Scanamo.putAll[Sector](client)(tableName)(all.toSet).size
  }
  
  def delete(sectors:Seq[Sector]) = {
    import com.gu.scanamo.query._
    val keys = sectors.map(_.sectorId).toSet
    Scanamo.deleteAll(client)(tableName)(UniqueKeys(KeyList('sectorId, keys))) 
    0
  }
  
  
  
  def getAllSectors = getAll
  
}