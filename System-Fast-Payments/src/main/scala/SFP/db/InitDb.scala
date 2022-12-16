package SFP.db

import SFP.db.AccountDb.accountTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

class InitDb(implicit val ec: ExecutionContext, db: Database) {
  def prepare(): Future[_] = {
      db.run(accountTable.schema.createIfNotExists)
  }
}
