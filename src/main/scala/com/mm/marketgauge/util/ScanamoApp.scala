package com.mm.marketgauge.util
import com.gu.scanamo.{Table, Scanamo, DynamoFormat}
import com.gu.scanamo.syntax._
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType._
import com.amazonaws.services.dynamodbv2._
import amazon.util.LocalDynamoDB
import com.mm.marketgauge.entities.{Sector, SharePrice}



object ScanamoApp extends App {

  case class Farm(animals: List[String])
  case class Farmer(name: String, age: Long)

  
  
  val client = LocalDynamoDB.client("http://localhost:9999")
  //val sectorsTableResult = LocalDynamoDB.createTable(client)("sectors")('sectorId -> N)
  //we can simply put and get items from Dynamo, without boilerplate or reflection
  
  //val table = Table[Farmer]("farmer")
  // table: com.gu.scanamo.Table[Farmer] = Table(farmer)

  Scanamo.put(client)("sectors")(Sector("sectorName", "^UXX", -3, "goog"))
  //Scanamo.exec(client)(table.put(Farmer("McDonald", 157L)))
  // res2: com.amazonaws.services.dynamodbv2.model.PutItemResult = {}
  val res = Scanamo.get[Sector](client)("sectors")('sectorId -> -2)
  println(s"Result is :$res")
  //println(Scanamo.exec(client)(table.get('name -> "McDonald")))

}


object ScanamoObj extends {

  val client = LocalDynamoDB.client("http://localhost:9999")
  //val sectorsTableResult = LocalDynamoDB.createTable(client)("sectors")('sectorId -> N)
  //we can simply put and get items from Dynamo, without boilerplate or reflection
  
  //val table = Table[Farmer]("farmer")
  // table: com.gu.scanamo.Table[Farmer] = Table(farmer)

  
  def createSector(sectorId:Int) = {
    Scanamo.put(client)("sectors")(Sector(s"sectorName$sectorId", s"^UXX$sectorId", sectorId, "goog"))  
  }
  
  
  def querySectors(sectorId:Int) = {
    val client = LocalDynamoDB.client("http://localhost:9999")
    val res = Scanamo.get[Sector](client)("sectors_test")('sectorId -> sectorId)
    println(s"Result is :$res")
  }

  
  def queryLocalSharePrices = {
    val client = LocalDynamoDB.client("http://localhost:9999")
    val result = Scanamo.scan[SharePrice](client)("share_prices")
    
    val res = result.foldLeft(Seq[SharePrice]())((accumulator, item) => {
            item match {
              case Right(sector) => sector +: accumulator
              case Left(error) => accumulator
            }
         })
    println("Result is:" + res)
    
  }
  
  def createSharePrice(ticker:String, localDb:Boolean) = {
    val client = localDb match {
      case true => getLocalClient
      case false => getAmazonDbClient
    }
    import java.text.SimpleDateFormat
    val df = new SimpleDateFormat("YYYY-MM-dd")
    val dateStr = df.format(new java.util.Date())
    Scanamo.put(client)("share_prices")(
        SharePrice(ticker, 0.0, dateStr,
                 0.0, 0.0,
                 0.0, "FOOB" +ticker,
                 0.0, 0.0, "xx" + ticker))  
  }
  
  
  
  
  def createLocalSectorTable = {
    import com.amazonaws.services.dynamodbv2._
    import com.amazonaws.services.dynamodbv2.model._
    import collection.JavaConverters._
      
    val client = getLocalClient
    
    LocalDynamoDB.createTable(client)("sectors_test")('ticker-> S,'sectorId -> N)
    
  }
  
  def createLocalSharePriceTable = {
    import com.amazonaws.services.dynamodbv2._
    import com.amazonaws.services.dynamodbv2.model._
    import collection.JavaConverters._
      
    val client = getLocalClient
    
    LocalDynamoDB.createTable(client)("share_prices")('ticker -> S,'asOfDate ->S )
    
  }
  
  
  
  
  def createSectorTable = {
    import com.amazonaws.services.dynamodbv2._
    import com.amazonaws.services.dynamodbv2.model._
    import collection.JavaConverters._
      
    val amazonClient = getAmazonDbClient
    //amazonClient.createTable("sectors_test")('sectorId -> N)
    
  }
  
  
  
  
  def readFromAmazonDb(sectorId:Int):Unit = {
    // reading sectorId from Amazon db
    val client = getAmazonDbClient
    println(s"Attempting to retrieve data for SectorId:$sectorId")
    val res = Scanamo.get[Sector](client)("sectors_test")('sectorId -> sectorId)
    println(s"Result from connecting to AWS Dynamodb is:$res")
  }
  
  def createAmazonSector(sectorId:Int):Unit = {
    val client = getAmazonDbClient
    Scanamo.put(client)("sectors_test")(Sector(s"sectorName$sectorId", s"^UXX$sectorId", sectorId, "goog"))  
  }
  
  
  def createAmazonSectorForFTSE:Unit = {
    val sectors = Seq(("^N225",-1000),
                        ("^HSI",-1001),
                        ("^DJI",-1002),
                        ("^FTSE",-1003),
                        ("SHP.L",-1004),
                        ("CCC.L",-1005),
                        ("RIO.L",-1006),
                        ("NG.L",-1007),
                        ("LLOY.L",-1008),
                        ("BLT.L",-1009),
                        ("DGE.L",-1010),
                        ("CRDA.L",-1011),
                        ("AZN.L",-1012),
                        ("BATS.L",-1013),
                        ("GSK.L",-1014),
                        ("BP.L",-1015),
                        ("RBS.L",-1016),
                        ("BARC.L",-1017),
                        ("HSBA.L",-1018),
                        ("VOD.L",-1019 ),
                        ("ULVR.L",-1020),
                        ("GBPUSD=X.",-1021),
                        ("GBPEUR=X",-1022),
                        ("GBPCHF=X",-1024),
                        ("USDJPY=X",-1025))
                        
                        
    val client = getAmazonDbClient
    for ((ticker, sectorId) <-sectors) {
      Scanamo.put(client)("sectors_test")(Sector(s"ticker@$sectorId", ticker, sectorId, "goog"))
    }
  }
  
  
  
  def populateAmazonSectors(fileName:String):Unit = {
    import com.github.tototoshi.csv._
    println(s"Pouplating Scanamo table from fie $fileName")
    
    val fileContent = CSVReader.open(fileName).all()
    val sectors = fileContent.map(lst => Sector(lst(0), lst(1), lst(2).toInt, lst(3)))
    val client = getAmazonDbClient
    for (sect <- sectors) {
      println(s"Adding ${sect}")
      Scanamo.put(client)("sectors_test")(sect)
    }
      
  }
  
  def populateLocalSectors(fileName:String):Unit = {
    import com.github.tototoshi.csv._
    println(s"Pouplating local sector table from fie $fileName")
    
    val fileContent = CSVReader.open(fileName).all()
    val sectors = fileContent.map(lst => Sector(lst(0), lst(1), lst(2).toInt, lst(3)))
    val client = getLocalClient
    for (sect <- sectors) {
      println(s"Adding ${sect}")
      Scanamo.put(client)("sectors_test")(sect)
    }
      
  }
  
  
  def getLocalClient = {
    LocalDynamoDB.client("http://localhost:9999")
  }
  
  
  def getAmazonDbClient = {
    import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
    import com.amazonaws.regions.Regions
    val client = AmazonDynamoDBClientBuilder.standard()
                  .withRegion(Regions.US_WEST_2)
                  .build()
    client
  }

}

