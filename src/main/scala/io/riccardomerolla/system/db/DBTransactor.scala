package io.riccardomerolla.system.db

import cats.effect.*
import cats.implicits.*
import doobie.*
import doobie.hikari.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import zio.interop.catz.*
import zio.interop.catz.implicits.*
import zio.{Task, *}
import eu.timepit.refined.auto.*

import scala.concurrent.ExecutionContext

import io.riccardomerolla.system.config.{PostgresConfig, Config}
import io.riccardomerolla.system.db.DBTransactor

trait DBTransactor:
  val trx: UIO[Transactor[Task]]

object DBTransactor extends zio.Accessible[DBTransactor]

case class DBTransactorLive(trx: UIO[Transactor[Task]]) extends DBTransactor

object DBTransactorLive:
  private def makeTransactor(
      config: PostgresConfig,
      ec: ExecutionContext
  ): TaskManaged[Transactor[Task]] =
    HikariTransactor
      .newHikariTransactor[Task](
        config.className,
        config.url,
        config.user,
        config.password,
        ec
      )
      .toManagedZIO

  val managed: ZManaged[Config, Throwable, Transactor[Task]] = (for {
    dbConfig <- Config(_.dbConfig).toManaged
    ce <- ZIO.descriptor
      .map(_.executor.asExecutionContext)
      .toManaged
    xa <- makeTransactor(dbConfig, ce)
  } yield xa)

  val layer: RLayer[Config, DBTransactor] = ZLayer.fromManaged(
    managed.map(t => DBTransactorLive(UIO(t)))
  )