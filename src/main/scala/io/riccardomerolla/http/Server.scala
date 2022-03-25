package io.riccardomerolla.http

import zio.*
import cats.syntax.all.*
import io.riccardomerolla.http.routes.*
import io.riccardomerolla.system.config.*
import io.riccardomerolla.Environment.AppEnv
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits.*
import zio.*
import zio.interop.catz.*
import zio.interop.catz.implicits.*
import org.http4s.server.Router
import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import org.http4s.HttpRoutes
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.server.http4s.ztapir.ZHttp4sServerInterpreter
import eu.timepit.refined.auto.*

object Server:

  def run(): ZIO[AppEnv & Config, Throwable, Unit] = for {

    config <- Config(_.httpServer)
    swaggerRoutes = ZHttp4sServerInterpreter()
      .from(
        SwaggerInterpreter().fromServerEndpoints[RIO[AppEnv, *]](
          BusinessRoutes.endpoints,
          "Test",
          "0.1.0"
        )
      )
      .toRoutes
    routes = Router(
      "/" -> (BusinessRoutes.routes <+> swaggerRoutes)
    ).orNotFound
    _ <- BlazeServerBuilder[RIO[AppEnv, *]]
      .bindHttp(config.port, config.host)
      .withoutBanner
      .withHttpApp(routes)
      .serve
      .compile
      .drain
  } yield ()