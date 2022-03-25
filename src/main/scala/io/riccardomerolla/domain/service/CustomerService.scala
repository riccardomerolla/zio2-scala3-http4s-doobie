package io.riccardomerolla.domain.service

import zio.*
import io.riccardomerolla.domain.Models.Customer
import io.riccardomerolla.domain.repository.CustomerRepository
import io.riccardomerolla.domain.Errors.DomainError

trait CustomerService:
    def get(id: Long): RIO[CustomerRepository, Customer]

object CustomerService extends zio.Accessible[CustomerService]

case class CustomerServiceLive(repo: CustomerRepository)
    extends CustomerService:
  override def get(id: Long): RIO[CustomerRepository, Customer] =
    repo
      .getById(id)
      .flatMap(maybeCustomer =>
        ZIO.fromOption(maybeCustomer).mapError(_ => DomainError.CustomerNotFound(id))
      )

object CustomerServiceLive:
  val layer: URLayer[CustomerRepository, CustomerService] =
    (CustomerServiceLive(_)).toLayer[CustomerService]
