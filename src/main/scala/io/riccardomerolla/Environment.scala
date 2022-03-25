package io.riccardomerolla

import zio.*
import io.riccardomerolla.domain.service.CustomerService
import io.riccardomerolla.domain.repository.CustomerRepository

object Environment:
  
  type BusinessEnv = CustomerService & CustomerRepository & Clock
  
  type AppEnv = BusinessEnv