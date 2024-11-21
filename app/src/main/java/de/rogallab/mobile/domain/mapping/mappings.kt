package de.rogallab.mobile.domain.mapping

import de.rogallab.mobile.data.local.dtos.AddressDto
import de.rogallab.mobile.data.local.dtos.CarDto
import de.rogallab.mobile.data.local.dtos.MovieDto
import de.rogallab.mobile.data.local.dtos.PersonDto
import de.rogallab.mobile.domain.entities.Address
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.domain.entities.Movie
import de.rogallab.mobile.domain.entities.Person

fun PersonDto.toPerson(): Person = Person(
   firstName = firstName,
   lastName = lastName,
   email = email,
   phone = phone,
   imagePath = imagePath,
   id = id
)
fun Person.toPersonDto(): PersonDto = PersonDto(
   firstName = firstName,
   lastName = lastName,
   email = email,
   phone = phone,
   imagePath = imagePath,
   id = id
)

fun AddressDto.toAddress(): Address = Address(
   city = city,
   postCode = postCode,
   id = id,
   personId = personId
)
fun Address.toAddressDto(): AddressDto = AddressDto(
   city = city,
   postCode = postCode,
   id = id,
   personId = personId
)

fun CarDto.toCar(): Car = Car(
   maker = maker,
   model = model,
   id = id,
   personId = personId
)
fun Car.toCarDto(): CarDto = CarDto(
   maker = maker,
   model = model,
   id = id,
   personId = personId
)

fun MovieDto.toMovie(): Movie = Movie(
   title = title,
   director = director,
   id = id
)
fun Movie.toMovieDto(): MovieDto = MovieDto(
   title = title,
   director = director,
   id = id
)

