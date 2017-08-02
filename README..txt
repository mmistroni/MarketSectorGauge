TODO:

build abstractions
- YahooDownloaderTrait DONE
- PersistenceService DONE
- Test MongoDb connectivity via Scala DONE
- StockPrice DONE
- Company    DONE
- Sectors    DONE
- We need actors for uploading informations DROPPED. Using Static Loader instead
- we need to expose a REST Service, potentially using Akka instead of Spray  - LATER STAGE OF THE PROJECT -

that is all we need to download data.
we leave the calculation to perhaps a spark process which is kicked off via an IOT


Example running to download share prices:

scala -Dcustom.shares=HSBC -Dlogback.configurationFile=./logback-debug.xml target\scala-2.11\marketsectorgauge.jar tester


Provision Service decompositions


so the Launcher is this

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")
  
  implicit val system = ActorSystem("quiz-management-service")
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10 seconds)

  val api = system.actorOf(Props(new RestInterface(false))) ==> this kick it off

  IO(Http).ask(Http.Bind(listener = api, interface = host, port = port))
    .mapTo[Http.Event]
    .map {
      case Http.Bound(address) =>
        println(s"REST interface bound to $address")
      case Http.CommandFailed(cmd) =>
        println("REST interface could not bind to " +
          s"$host:$port, ${cmd.failureMessage}")
        system.shutdown()
    }
}

class RestInterface(productionFlag:Boolean)(implicit val executionContext: ExecutionContext) extends HttpServiceActor with Resources {

  def receive = runRoute(routes)

  val persistence = new PersistenceService
  /**
  persistence.createSchema() onSuccess { 
      
      case _ => println("Creating dataset");persistence.createDataset()
    
  }
  * 
  */
  val provisionService = new PersistedProvisionService(persistence)

  val routes: Route = provisionRoutes

}

trait Resources extends ProvisionResource

trait ProvisionResource extends MyHttpService {
  val provisionService: ProvisionService == this is instantiated above
  
  def provisionRoutes: Route = pathPrefix("provisions") {
    pathEnd {
      post {
        entity(as[Provision]) { question =>
          completeWithLocationHeader(
            resourceId = provisionService.createProvision(question),
            ifDefinedStatus = Created.intValue, ifEmptyStatus = 409)
        }
      } ~
      get {
        complete(provisionService.getAllProvisions)
      }
    } ~
    path ("provisionType" / Segment){provisionType =>

The PersistedPRovisionService just extends ProvisionService whose methods return
a Future.
When using Slick that will do it, but when using normal class we will have to wrap everything into a Future


