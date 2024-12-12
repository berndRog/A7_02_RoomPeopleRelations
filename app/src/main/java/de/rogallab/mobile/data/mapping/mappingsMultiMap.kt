package de.rogallab.mobile.data.mapping

import de.rogallab.mobile.data.dtos.AddressDto
import de.rogallab.mobile.data.dtos.CarDto
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.domain.entities.Address
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.domain.entities.Movie
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logVerbose

fun multiMapToPersonWithAddress(
   multiMap: Map<PersonDto, AddressDto?>
): Person {
   var person: Person = multiMap.keys.first().toPerson()
   var addressDto: AddressDto? = multiMap.values.first()
   var address: Address? = null
   addressDto?.let { it: AddressDto ->
      // set personId in address and transform to Address
      address = it.copy(personId = person.id).toAddress()
      // set address in person
      person = person.copy(address = address)
   }
   logVerbose("<-toPersonWithAddress", "$person")
   return person
}

fun multiMapToPersonWithCars(
   multiMap: Map<PersonDto, List<CarDto>>
): Person {
   var person: Person = multiMap.keys.first().toPerson()
   val cars: List<Car> = multiMap.values.first().map {
      carDto: CarDto -> carDto.copy(personId = person.id).toCar()
   }
   person = person.copy(cars = cars.toMutableList())
   return person
}

fun multiMapToPersonWithMovies(
   multiMap: Map<PersonDto, List<MovieDto>>
): Person {
   var person: Person = multiMap.keys.first().toPerson()
   val movies: List<Movie> = multiMap.values.first().map {
      movieDto: MovieDto -> movieDto.toMovie()
   }
   person = person.copy(movies = movies.toMutableList())
   return person
}