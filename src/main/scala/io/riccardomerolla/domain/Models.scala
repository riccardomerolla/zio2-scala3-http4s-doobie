package io.riccardomerolla.domain

import eu.timepit.refined.api.Refined
import eu.timepit.refined.collection.NonEmpty
import eu.timepit.refined.types.string.NonEmptyString
import cats.data.EitherNel
import cats.implicits.*
import io.riccardomerolla.domain.Errors.ValidationError
import io.riccardomerolla.domain.Errors.DomainError.*

object Models:

  final case class Customer(
      id: Long,
      firstName: String Refined NonEmpty,
      lastName: String Refined NonEmpty
  )

  object Customer:
    def create(
        firstName: String,
        lastName: String
    ): EitherNel[ValidationError, Customer] =
      (
        NonEmptyString.from(firstName).leftMap(ValidationError.EmptyField("firstName", _)).toEitherNel,
        NonEmptyString.from(lastName).leftMap(ValidationError.EmptyField("lastName", _)).toEitherNel
      ).parMapN(Customer(-1, _, _))