package SFP.db

import SFP.model.Account
import slick.jdbc.PostgresProfile.api._

import java.util.UUID

object AccountDb {
  class AccountTable(tag: Tag) extends Table[Account](tag, "accounts"){
    val id = column[UUID]("id", O.PrimaryKey)
    val balance = column[Int]("balance")

    def * = (id, balance) <> ((Account.apply _).tupled, Account.unapply _)
  }

  val accountTable = TableQuery[AccountTable]
}
