package SFP.repository

import SFP.db.AccountDb._
import SFP.model.{Account, CreateAccount, ReplenishAccount, TransferMoney, WithdrawAccount}
import slick.jdbc.PostgresProfile.api._

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class AccountRepositoryDb(implicit val ec: ExecutionContext, db: Database) extends AccountRepository{
  override def list(): Future[Seq[Account]] = {
      db.run(accountTable.result)
  }

  override def get(id: UUID): Future[Account] = {
      db.run(accountTable.filter(_.id === id).result.head)
  }

  def find(id: UUID): Future[Option[Account]] = {
      db.run(accountTable.filter(_.id === id).result.headOption)
  }

  override def createAccount(createAccount: CreateAccount): Future[Account] = {
      val account = Account()
      db.run(accountTable += account).flatMap(_ => get(account.id))
  }

  override def replenishAccount(replenishAccount: ReplenishAccount): Future[Option[Account]] = {
      val query = accountTable
          .filter(_.id === replenishAccount.id)
          .map(_.balance)
      for {
            existed <- db.run(query.result.headOption)
            _ <- db.run {
              if (replenishAccount.amount <= 0) {query.update(existed.get)}
              else {query.update(existed.get + replenishAccount.amount)}
            }
            res <- find(replenishAccount.id)
      } yield res
  }

  override def withdrawAccount(withdrawAccount: WithdrawAccount): Future[Option[Account]] = {
      val query = accountTable
          .filter(_.id === withdrawAccount.id)
          .map(_.balance)
      for {
            existed <- db.run(query.result.headOption)
            _ <- db.run {
              if (withdrawAccount.amount <= 0 || withdrawAccount.amount > existed.get) {query.update(existed.get)}
              else {query.update(existed.get - withdrawAccount.amount)}
            }
            res <- find(withdrawAccount.id)
      } yield res
  }

  override def transferMoney(transferMoney: TransferMoney): Future[Option[Account]] = {
      val WithdrawFromAccount = WithdrawAccount(transferMoney.idFrom, transferMoney.amount)
      val queryFrom = accountTable
          .filter(_.id === transferMoney.idFrom)
          .map(_.balance)
      val queryTo = accountTable
          .filter(_.id === transferMoney.idTo)
          .map(_.balance)
      for {
            existedFrom <- db.run(queryFrom.result.headOption)
            existedTo <- db.run(queryTo.result.headOption)
            _ <- db.run {
              if (transferMoney.amount <= 0 || transferMoney.amount > existedFrom.get) {queryFrom.update(existedFrom.get)}
              else {
                withdrawAccount(WithdrawFromAccount)
                queryTo.update(existedTo.get + transferMoney.amount)
              }
            }
            res <- find(transferMoney.idFrom)
      } yield res
  }

  override def deleteAccount(id: UUID): Future[Unit] = {
      db.run(accountTable.filter(_.id === id).delete).map(_ => ())
  }
}
