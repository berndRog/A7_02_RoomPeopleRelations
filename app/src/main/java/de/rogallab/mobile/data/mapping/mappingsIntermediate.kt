package de.rogallab.mobile.data.mapping

import de.rogallab.mobile.data.dtos.AddressDto
import de.rogallab.mobile.data.dtos.CarDto
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.local.database.intermediate.PersonWithAddress
import de.rogallab.mobile.data.local.database.intermediate.PersonWithCars
import de.rogallab.mobile.data.local.database.intermediate.PersonWithMoviesByCrossRef

import de.rogallab.mobile.domain.entities.Address
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.domain.entities.Movie
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logVerbose

fun PersonWithAddress.toPersonWithAddress(): Person {
   var person = personDto.toPerson()
   var address: Address? = null
   addressDto?.let { it: AddressDto ->
      // set personId in address and transform to Address
      address = it.copy(personId = person.id).toAddress()
      // set address in person
      person = person.copy(address = address)
      // address = address?.copy(person = person)
   }
   logVerbose("<-toPersonWithAddress", "$person")
   return person
}

fun PersonWithCars.toPersonWithCars(): Person {
   var person: Person = personDto.toPerson()
   var cars: List<Car> = carDtos.map { carDto: CarDto ->
      carDto.copy(personId = person.id).toCar()
   }
   person = person.copy(cars = cars.toMutableList())
   return person
}

fun PersonWithMoviesByCrossRef.toPersonWithMovies(): Person {
   var person: Person = personDto.toPerson()
   var movies: List<Movie> = movieDtos.map { movieDto: MovieDto ->
      movieDto.toMovie()
   }
   person = person.copy(movies = movies.toMutableList())
   return person
}

