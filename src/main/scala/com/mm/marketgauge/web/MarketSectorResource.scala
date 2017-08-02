package com.mm.marketgauge.web


import scala.concurrent.Future
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.Done
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.mm.marketgauge.dao.Database
import spray.json._
import scala.concurrent.ExecutionContext
import scala.io.StdIn
import com.mm.marketgauge.entities.SharePrice
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(Item)
  implicit val orderFormat = jsonFormat1(Order) // contains List[Item]
  implicit val sharePriceFormat = jsonFormat2(SharePriceDummy)
}


trait MarketSectorResource extends com.mm.marketgauge.util.LogHelper with JsonSupport {

  // formats for unmarshalling and marshalling
  val handler: MarketSectorHandler

  val marketSectorRoutes: Route =
       /**
       get { to find out why jsonFormat cannot marshal a  simple SharePrice case class
         pathPrefix("item" / Segment) { ticker =>
           // there might be no item for a given ticker
           val maybeItem: Future[Option[SharePrice]] = handler.fetchSharePrice(ticker)
           logger.info(s"Fetching item:$ticker")
           logger.info(s"OBtained:$maybeItem")
           onSuccess(maybeItem) {
             case Some(item) => complete(item)
             case None       => complete(StatusCodes.NotFound)
           }
         }
      } ~**/
      get {
        pathPrefix("item" / LongNumber) { id =>
          // there might be no item for a given id
          val maybeItem: Future[Option[Item]] = handler.fetchItem(id)
          logger.info(s"Fetching item:$id")
          logger.info(s"OBtained:$maybeItem")
          onSuccess(maybeItem) {
            case Some(item) => complete(item)
            case None       => complete(StatusCodes.NotFound)
          }
        }
      } ~
      post {
        path("create-order") {
          entity(as[Order]) { order =>
            val saved: Future[Done] = handler.saveOrder(order)
            onComplete(saved) { done =>
              complete("order created")
            }
          }
        }
      }

}