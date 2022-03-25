package io.riccardomerolla.http

import cats.data.EitherNel

import io.riccardomerolla.domain.Models.*
import eu.timepit.refined.auto.*
import io.riccardomerolla.domain.Errors.*

object DTO:

  trait CustomerInfo:
    val firstName: String
    val lastName: String

    def toDomain: EitherNel[ValidationError, Customer] =
      Customer.create(firstName, lastName)


  final case class CustomerData(id: Long, firstName: String, lastName: String) extends CustomerInfo
    
  final case class CreateCustomerData(firstName: String, lastName: String) extends CustomerInfo
   
  final case class UpdateCustomerData(firstName: String, lastName: String) extends CustomerInfo

  extension (c: Customer)
    def toData: CustomerData = CustomerData(c.id, c.firstName, c.lastName)