package com.mm.marketgauge.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future


object WebClient extends App {

implicit val system = ActorSystem()
implicit val materializer = ActorMaterializer()
implicit val executionContext = system.dispatcher


val responseFuture: Future[HttpResponse] =
  Http().singleRequest(HttpRequest(uri = "http://localhost:8080/item/11"))
  
responseFuture onSuccess {
  case resp => println(s"OBtaiinedL$resp")
}
  

  
  
}