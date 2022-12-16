package SFP.repository

import SFP.model._

import java.util.UUID
import scala.concurrent.Future


trait AccountRepository {
  def list(): Future[Seq[Account]]
  def get(id: UUID): Future[Account]
  def createAccount(account: CreateAccount): Future[Account]
  def replenishAccount(account: ReplenishAccount): Future[Option[Account]]
  def withdrawAccount(account: WithdrawAccount): Future[Option[Account]]
  def transferMoney(account: TransferMoney): Future[Option[Account]]
  def deleteAccount(id: UUID): Future[Unit]
}
