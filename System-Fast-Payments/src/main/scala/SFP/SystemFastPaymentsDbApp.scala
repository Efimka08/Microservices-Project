package SFP

import SFP.db.InitDb
import SFP.repository.AccountRepositoryDb
import SFP.route._
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory
import slick.jdbc.PostgresProfile.api._

object SystemFastPaymentsDbApp extends App {
  implicit val system: ActorSystem = ActorSystem("SystemFastPaymentsApp")
  implicit val ec = system.dispatcher
  implicit val db = Database.forConfig("database.postgres")

  new InitDb().prepare()
  val repository = new AccountRepositoryDb
  val helloRoute = new HelloRoute().route
  val accountRoute = new AccountRoute(repository).route

  val port = ConfigFactory.load().getInt("port")

  Http().newServerAt("0.0.0.0", port = port).bind(helloRoute ~ accountRoute)
}
