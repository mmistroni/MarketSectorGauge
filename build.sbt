enablePlugins(JavaServerAppPackaging)

name := "MarketSectorGauge"

version := "0.1"

organization := "com.mm"

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-deprecation")

assembleArtifact in packageScala := true
assembleArtifact in packageDependency := true
assemblyJarName in assembly := "marketsectorgauge.jar"

startDynamoDBLocal := startDynamoDBLocal.dependsOn(compile in Test).value
test in Test := (test in Test).dependsOn(startDynamoDBLocal).value
testOnly in Test := (testOnly in Test).dependsOn(startDynamoDBLocal).value
testOptions in Test += dynamoDBLocalTestCleanup.value
dynamoDBLocalPort := 8888




mainClass in assembly :=   Some("com.mm.marketgauge.loaders.LoaderExecutor")

assemblyMergeStrategy in assembly := {
  case PathList("ch", "qos", xs @ _*)         => MergeStrategy.first
  case PathList("com", "sun", xs @ _*)         => MergeStrategy.first
  case PathList("org", "slf4j", xs @ _*)         => MergeStrategy.first
  case PathList("akka", "http", xs @ _*)         => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}




resolvers ++= Seq("spray repo" at "http://repo.spray.io/",
                "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
                "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
                "sprest snapshots" at "http://markschaake.github.com/releases")

libraryDependencies ++= {
  val AkkaVersion       = "2.4.11"
  val akkaHttpVersion =   "3.0.0-RC1" //"2.4.2-RC2"
  Seq(
    "com.typesafe.akka" %% "akka-slf4j"      % AkkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.1.2",
    "com.h2database" % "h2" % "1.4.190",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "org.specs2" %% "specs2" % "2.3.11" % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-testkit" % AkkaVersion,
    "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % "test",
    "org.mockito" % "mockito-core" % "1.9.5",
    "org.slf4j" % "slf4j-api" % "1.7.5",
    "org.slf4j" % "slf4j-simple" % "1.7.5",
    "org.clapper" %% "grizzled-slf4j" % "1.0.2",
    "org.reactivemongo" %% "reactivemongo" % "0.11.14",
    "com.github.tototoshi" %% "scala-csv" % "1.3.3",
    "org.mongodb" %% "casbah" % "2.8.0",
    "com.mm" %% "sparkutilities" % "1.3" ,
    "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % "1.50.5" % "test",
    "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.2" % "test",
    "com.typesafe.akka" %% "akka-http-spray-json"  % akkaHttpVersion,
    "com.gu" %% "scanamo" % "0.9.5",
    "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.172",
     "org.json4s" % "json4s-native_2.11" % "3.3.0" 
  )
  
}

