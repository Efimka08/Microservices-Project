package SFP

import SFP.repository.AccountRepositoryInMemory
import SFP.route._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

object SystemFastPaymentsHttpApp extends App {
  implicit val system: ActorSystem = ActorSystem("SystemFastPaymentsApp")
  implicit val ec = system.dispatcher
  val repository = new AccountRepositoryInMemory
  val helloRoute = new HelloRoute().route
  val accountRoute = new AccountRoute(repository).route

//  val AccIdFirst = repository.createAccount(CreateAccount()).id
//  val AccIdSecond = repository.createAccount(CreateAccount()).id
//  repository.replenishAccount(ReplenishAccount(AccIdFirst, 862))
//  repository.replenishAccount(ReplenishAccount(AccIdSecond, 1103))

  Http().newServerAt("0.0.0.0", port = 8080).bind(helloRoute ~ accountRoute)
}
