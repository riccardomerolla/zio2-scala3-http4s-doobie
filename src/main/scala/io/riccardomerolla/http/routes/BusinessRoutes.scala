package io.riccardomerolla.http.routes

import org.http4s.HttpRoutes
import sttp.tapir.PublicEndpoint
import sttp.tapir.ztapir.*
import sttp.tapir.json.circe.*
import sttp.tapir.generic.auto.*
import io.circe.generic.auto.*
import sttp.tapir.server.http4s.*
import sttp.tapir.server.http4s.ztapir.*
import zio.*
import zio.interop.catz.*
import io.riccardomerolla.Environment.BusinessEnv
import io.riccardomerolla.http.DTO.*
import sttp.model.StatusCode
import io.riccardomerolla.http.ErrorInfo
import io.riccardomerolla.http.ErrorInfo.*
import sttp.tapir.EndpointOutput.OneOf
import eu.timepit.refined.auto.*
import io.riccardomerolla.domain.service.CustomerService

object BusinessRoutes:

  val httpErrors: OneOf[ErrorInfo, ErrorInfo] = oneOf[ErrorInfo](
    oneOfVariant(StatusCode.InternalServerError, jsonBody[InternalServerError]),
    oneOfVariant(StatusCode.BadRequest, jsonBody[BadRequest]),
    oneOfVariant(StatusCode.NotFound, jsonBody[NotFound])
  )

  val getCustomer: PublicEndpoint[Long, ErrorInfo, CustomerData, Any] =
    endpoint.get
      .in("customer" / path[Long]("id"))
      .out(jsonBody[CustomerData])
      .errorOut(httpErrors)

  // endpoints

  def getCustomerEndpoint: ZServerEndpoint[BusinessEnv, Any] =
    getCustomer.zServerLogic { (id: Long) =>
      CustomerService(_.get(id)).mapBoth(e => NotFound(e.getMessage), _.toData)
    }

  // routes
  val endpoints: List[ZServerEndpoint[BusinessEnv, Any]] = List(
    getCustomerEndpoint
  )

  val routes: HttpRoutes[RIO[BusinessEnv, *]] =
    ZHttp4sServerInterpreter()
      .from(
        endpoints
      )
      .toRoutes
