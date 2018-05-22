package com.mm.marketgauge.persistence.dynamodb
import com.mm.marketgauge.persistence.PersistenceServiceComponent
import amazon.util.LocalDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.regions.Regions
    

trait LocalDyamoDbPersistenceServiceComponent extends PersistenceServiceComponent {
  
  override val sectorRepository = new LocalSectorRepository{}
  
  override val companyRepository = null
  
  override val companyRepoRepository = null
  
  override val sharePriceRepository = new LocalSharePriceRepository{}
    
}

trait RemoteDynamoDbPersistenceServiceComponent extends PersistenceServiceComponent {
  
  override val sectorRepository = new RemoteSectorRepository{}
  
  override val companyRepository = null
  
  override val companyRepoRepository = null
  
  override val sharePriceRepository = new RemoteSharePriceRepository{}
    
}




trait LocalSectorRepository extends SectorRepository {
  override lazy val client = LocalDynamoDB.client("http://localhost:9999")
}

trait LocalSharePriceRepository extends SharePriceRepository {
  override lazy val client = LocalDynamoDB.client("http://localhost:9999")
}


trait RemoteSectorRepository extends SectorRepository {
  override lazy val client = AmazonDynamoDBClientBuilder.standard()
                  .withRegion(Regions.US_WEST_2)
                  .build()
}

trait RemoteSharePriceRepository extends SharePriceRepository {
  override lazy val client = AmazonDynamoDBClientBuilder.standard()
                  .withRegion(Regions.US_WEST_2)
                  .build()
}

