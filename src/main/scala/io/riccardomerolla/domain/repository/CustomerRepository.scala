package io.riccardomerolla.domain.repository

import zio.*
import cats.implicits.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.postgres.implicits.*
import doobie.refined.implicits.*
import doobie.*
import zio.interop.catz.*
import zio.interop.catz.implicits.*
import doobie.util.transactor.Transactor
import io.riccardomerolla.domain.Models.Customer
import io.riccardomerolla.system.db.DBTransactor

trait CustomerRepository:
  def getById(id: Long): Task[Option[Customer]]

object CustomerRepository extends zio.Accessible[CustomerRepository]

case class CustomerRepositoryLive(trx: Transactor[Task])
    extends CustomerRepository:

  override def getById(id: Long): Task[Option[Customer]] =
    sql"""SELECT * FROM CUSTOMERS WHERE ID = $id """
      .query[Customer]
      .option
      .transact(trx)

object CustomerRepositoryLive:
  val layer: URLayer[DBTransactor, CustomerRepository] = ZLayer.fromZIO(
    DBTransactor(_.trx).map(CustomerRepositoryLive(_))
  )
