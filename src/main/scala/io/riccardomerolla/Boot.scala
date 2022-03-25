package io.riccardomerolla

import io.riccardomerolla.system.config.*
import io.riccardomerolla.system.db.*
import io.riccardomerolla.http.Server
import io.riccardomerolla.domain.service.CustomerServiceLive
import io.riccardomerolla.domain.repository.CustomerRepositoryLive
import io.riccardomerolla.system.db.DBTransactor
import io.riccardomerolla.Environment.AppEnv

import zio.*

object Boot extends ZIOApp:

  override type Environment = AppEnv & Config & ZEnv

  override val tag: EnvironmentTag[Environment] = EnvironmentTag[Environment]

  override def layer: ZLayer[ZIOAppArgs, Throwable, Environment] =
    ZLayer.make[AppEnv & Config & ZEnv](
      ZEnv.live,
      ConfigLive.layer,
      CustomerServiceLive.layer,
      CustomerRepositoryLive.layer,
      DBTransactorLive.layer
    )

  override def run: ZIO[Environment & ZEnv & ZIOAppArgs, Any, ExitCode] =
    Server
      .run()
      .tapError(err => ZIO.logError(err.getMessage))
      .exitCode