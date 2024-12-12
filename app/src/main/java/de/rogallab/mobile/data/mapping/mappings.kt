package de.rogallab.mobile.data.mapping

import de.rogallab.mobile.data.dtos.AddressDto
import de.rogallab.mobile.data.dtos.CarDto
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.dtos.TicketDto
import de.rogallab.mobile.domain.entities.Address
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.domain.entities.Movie
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.entities.Ticket

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

fun joinPerson(
   personDto: PersonDto,
   addressDto: AddressDto?,
   carDtos: List<CarDto>?,
   movieDtos: List<MovieDto>?,
   ticketDtos: List<TicketDto>?
): Person {
   var person = personDto.toPerson()

   addressDto?.let { it: AddressDto ->
      // set personId in address and transform to Address
      val address: Address = it.copy(personId = person.id).toAddress()
      // set address in person
      person = person.copy(address = address)
   }

   carDtos?.let { it: List<CarDto> ->
      val cars: List<Car> = it.map { it.copy(personId = person.id).toCar() }
      person = person.copy(cars = cars.toMutableList())
   }

   movieDtos?.let { it: List<MovieDto> ->
      val movies: List<Movie> = it.map { it.toMovie() }
      person = person.copy(movies = movies.toMutableList())
   }

   ticketDtos?.let { it: List<TicketDto> ->
      val tickets: List<Ticket> = it.map { it.toTicket() }
      person = person.copy(tickets = tickets.toMutableList())
   }

   return person
}


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
   year = year,
   id = id
)
fun Movie.toMovieDto(): MovieDto = MovieDto(
   title = title,
   director = director,
   year = year,
   id = id
)

fun TicketDto.toTicket(): Ticket = Ticket(
   dateTime = this.dateTime,
   seat = this.seat,
   id = this.id,
   personId = this.personId,
   movieId = this.movieId
)
fun Ticket.toTicketDto(): TicketDto = TicketDto(
   dateTime = this.dateTime,
   seat = this.seat,
   id = this.id,
   personId = this.personId,
   movieId = this.movieId
)



