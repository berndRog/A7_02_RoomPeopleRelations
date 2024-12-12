package de.rogallab.mobile.data.local.seed

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import de.rogallab.mobile.R
import de.rogallab.mobile.data.dtos.AddressDto
import de.rogallab.mobile.data.dtos.CarDto
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.dtos.PersonMovieCrossRefDto
import de.rogallab.mobile.data.dtos.TicketDto
import de.rogallab.mobile.data.local.io.deleteFileOnStorage
import de.rogallab.mobile.data.local.io.writeImageToStorage
import de.rogallab.mobile.domain.utilities.createUuid
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logVerbose
import kotlinx.datetime.LocalDateTime
import kotlin.random.Random

class Seed(
   private val context: Context,
   private val resources: Resources
) {
   private val _imagesUrl = mutableListOf<String>()

   var personDtos: MutableList<PersonDto> = mutableListOf()
   var addressDtos: MutableList<AddressDto> = mutableListOf()
   var carDtos: MutableList<CarDto> = mutableListOf()
   var movieDtos: MutableList<MovieDto> = mutableListOf()
   var personMovieCrossRefs: MutableList<PersonMovieCrossRefDto> = mutableListOf()
   var ticketDtos: MutableList<TicketDto> = mutableListOf()

   //--- P E O P L E -----------------------------------------------------------------------
   fun createPerson(withImages: Boolean): Seed {

      val firstNames = mutableListOf(
         "Arne", "Berta", "Cord", "Dagmar", "Ernst", "Frieda", "Günter", "Hanna",
         "Ingo", "Johanna", "Klaus", "Luise", "Martin", "Nadja", "Otto", "Patrizia",
         "Quirin", "Rebecca", "Stefan", "Tanja", "Uwe", "Veronika", "Walter", "Xaver",
         "Yvonne", "Zwantje")
      val lastNames = mutableListOf(
         "Arndt", "Bauer", "Conrad", "Diehl", "Engel", "Fischer", "Graf", "Hoffmann",
         "Imhoff", "Jung", "Klein", "Lang", "Meier", "Neumann", "Olbrich", "Peters",
         "Quart", "Richter", "Schmidt", "Thormann", "Ulrich", "Vogel", "Wagner", "Xander",
         "Yakov", "Zander")
      val emailProvider = mutableListOf("gmail.com", "icloud.com", "outlook.com", "yahoo.com",
         "t-online.de", "gmx.de", "freenet.de", "mailbox.org", "yahoo.com", "web.de")
      val random = Random(0)
      for (index in firstNames.indices) {
         val firstName = firstNames[index]
         val lastName = lastNames[index]
         val email =
            "${firstName.lowercase()}." +
               "${lastName.lowercase()}@" +
               "${emailProvider.random()}"
         val phone =
            "0${random.nextInt(1234, 9999)} " +
               "${random.nextInt(100, 999)}-" +
               "${random.nextInt(10, 9999)}"
         val personDto = PersonDto(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            imagePath = null,
            id = createUuid(index + 1, 1)
         )
         logVerbose("<-Seed", "personDto: $personDto")
         personDtos.add(personDto)
      }

   //--- I M A G E S -----------------------------------------------------------------------
      if (!withImages) return this
      // convert the drawables into image files
      val drawables = mutableListOf<Int>()
      drawables.add(0, R.drawable.man_1)
      drawables.add(1, R.drawable.man_2)
      drawables.add(2, R.drawable.man_3)
      drawables.add(3, R.drawable.man_4)
      drawables.add(4, R.drawable.man_5)
      drawables.add(5, R.drawable.man_6)
      drawables.add(6, R.drawable.woman_1)
      drawables.add(7, R.drawable.woman_2)
      drawables.add(8, R.drawable.woman_3)
      drawables.add(9, R.drawable.woman_4)
      drawables.add(10, R.drawable.woman_5)

      drawables.forEach { it: Int ->  // drawable id
         val bitmap = BitmapFactory.decodeResource(resources, it)
         bitmap?.let { itbitm ->
            writeImageToStorage(context, itbitm)?.let { uriPath: String? ->
               uriPath?.let { _imagesUrl.add(uriPath) }
            }
         }
      }
      if (_imagesUrl.size == 11) {
         personDtos[0] = personDtos[0].copy(imagePath = _imagesUrl[0])
         personDtos[1] = personDtos[1].copy(imagePath = _imagesUrl[6])
         personDtos[2] = personDtos[2].copy(imagePath = _imagesUrl[1])
         personDtos[3] = personDtos[3].copy(imagePath = _imagesUrl[7])
         personDtos[4] = personDtos[4].copy(imagePath = _imagesUrl[2])
         personDtos[5] = personDtos[5].copy(imagePath = _imagesUrl[8])
         personDtos[6] = personDtos[6].copy(imagePath = _imagesUrl[3])
         personDtos[7] = personDtos[7].copy(imagePath = _imagesUrl[9])
         personDtos[8] = personDtos[8].copy(imagePath = _imagesUrl[4])
         personDtos[9] = personDtos[9].copy(imagePath = _imagesUrl[10])
         personDtos[10] = personDtos[10].copy(imagePath = _imagesUrl[5])
      }
      return this
   }

   //--- A D D R E S s E S ------------------------------------------------------------------
   fun createAddresses(): Seed {
      val cities = mutableListOf("Berlin", "Hamburg", "München", "Köln", "Frankfurt",
         "Stuttgart", "Düsseldorf", "Dortmund", "Essen", "Leipzig")
      val postCodes = mutableListOf("10115", "20095", "80331", "50667", "60311",
         "70173", "40210", "44135", "45127", "04109")
      for( i in 0..cities.size-1) {
         val addressDto = AddressDto(
            city = cities[i],
            postCode = postCodes[i],
            id = createUuid(i+1, 2),
            personId = personDtos[i].id
         )
         logVerbose("<-Seed", "addressDto: $addressDto")
         addressDtos.add(addressDto)
      }
      return this
   }

   //--- C A R S ----------------------------------------------------------------------------
   fun createCars(): Seed {
      val makers = mutableListOf("Audi", "BMW", "Citroen", "Dacia", "Fiat", "Ford",
         "Honda", "Hyundai", "Jaguar", "Jeep", "Kia", "Land Rover", "Mazda",
         "Mercedes", "Mini", "Mitsubishi", "Nissan", "Opel", "Peugeot", "Porsche",
         "Renault", "Seat", "Skoda", "Smart", "Subaru", "Suzuki", "Tesla", "Toyota",
         "Volkswagen", "Volvo")
      val models = mutableListOf("A1", "X1", "C3", "Sandero", "500", "Fiesta",
         "Civic", "i10", "XE", "Renegade", "Rio", "Discovery", "3",
         "A-Klasse", "Cooper", "Space Star", "Micra", "Corsa", "208", "911",
         "Clio", "Ibiza", "Octavia", "Fortwo", "Impreza", "Swift", "Model 3",
         "Yaris", "Golf", "V40")
      for( i in 0..makers.size-1) {
         if(i == 0) {
            val carDto = CarDto(
               maker = makers[i],
               model = models[i],
               id = createUuid(i+1, 3),
               personId = personDtos[0].id
            )
            logVerbose("<-Seed", "carDto: $carDto")
            carDtos.add( carDto)
         } else if( i == 1 || i == 2) {
            val carDto = CarDto(
               maker = makers[i],
               model = models[i],
               id = createUuid(i+1, 3),
               personId = personDtos[1].id
            )
            logVerbose("<-Seed", "carDto: $carDto")
            carDtos.add( carDto)
         } else if(i in 3..5) {
            val carDto = CarDto(
               maker = makers[i],
               model = models[i],
               id = createUuid(i+1, 3),
               personId = personDtos[2].id
            )
            logVerbose("<-Seed", "carDto: $carDto")
            carDtos.add( carDto)
         } else if(i in 6..9) {
            val carDto = CarDto(
               maker = makers[i],
               model = models[i],
               id = createUuid(i+1, 3),
               personId = personDtos[3].id
            )
            logVerbose("<-Seed", "carDto: $carDto")
            carDtos.add( carDto)
         } else {
            val carDto = CarDto(
               maker = makers[i],
               model = models[i],
               id = createUuid(i+1, 3),
               personId = null
            )
            logVerbose("<-Seed", "carDto: $carDto")
            carDtos.add( carDto)
         }
      }
      return this
   }

   //--- M O V I E S ----------------------------------------------------------------------------
   fun createMovies():Seed {
      val titles = mutableListOf(
         "Der Pate", "The Dark Knight", "Herr der Ringe: Rückkehr der Könige",
         "Pulp Fiction", "Schindler's Liste", "Inception",
         "Forrest Gump", "Matrix", "Fight Club",
         "Dark Knight rises", "Avatar", "The Wolf of Wall Street",
         "Django Unchained", "Dune", "The Batman"
      )
      val directors = mutableListOf(
         "Francis Ford Coppola", "Christopher Nolan", "Peter Jackson",
         "Quentin Tarantino", "Steven Spielberg", "Christopher Nolan",
         "Robert Zemeckis", "Lana&Lilly Wachowski", "David Fincher",
         "Christopher Nolan", "James Cameron", "Martin Scorsese",
         "Quentin Tarantino", "Denis Villeneuve", "Matt Reeves"
      )
      val year = mutableListOf(
         1972, 2008, 2003,
         1994, 1994, 2010,
         1994, 1999, 1999,
         2012, 2012, 2014,
         2013, 2021, 2022
      )

      for (i in titles.indices) {
         val movieDto = MovieDto(
            title = titles[i],
            director = directors[i],
            year = year[i],
            createUuid(i+1, 4)
         )
         logVerbose("<-Seed", "movieDto: $movieDto")
         movieDtos.add(movieDto)
      }

      // create person-movie cross reference
      val personMovieCrossRef = PersonMovieCrossRefDto(personDtos[0].id, movieDtos[0].id)
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[0].id, movieDtos[0].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[0].id, movieDtos[2].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[0].id, movieDtos[4].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[0].id, movieDtos[6].id))

      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[1].id, movieDtos[1].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[1].id, movieDtos[3].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[1].id, movieDtos[5].id))

      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[2].id, movieDtos[0].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[2].id, movieDtos[1].id))

      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[3].id, movieDtos[2].id))

      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[4].id, movieDtos[7].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[4].id, movieDtos[9].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[4].id, movieDtos[11].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[4].id, movieDtos[13].id))

      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[5].id, movieDtos[8].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[5].id, movieDtos[10].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[5].id, movieDtos[12].id))

      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[6].id, movieDtos[7].id))
      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[6].id, movieDtos[8].id))

      personMovieCrossRefs.add(PersonMovieCrossRefDto(personDtos[7].id, movieDtos[9].id))


      personMovieCrossRefs.forEach{
         logVerbose("<-Seed", "personMovieCrossRef: $it")
      }
      return this
   }

   //--- T I C K E T S ----------------------------------------------------------------------------
   fun createTickets():Seed {
      val ticket1Dto = TicketDto(
         dateTime = LocalDateTime(2023, 3, 1, 14, 0),
         seat = "A10",
         id = createUuid(1, 5),
         personId = personDtos[0].id,
         movieId = movieDtos[0].id
      )
      val ticket2Dto = TicketDto(
         dateTime = LocalDateTime(2023, 4, 2, 17, 0),
         seat = "B12",
         id = createUuid(2, 5),
         personId = personDtos[0].id,
         movieId = movieDtos[2].id
      )
      val ticket3Dto = TicketDto(
         dateTime = LocalDateTime(2023, 5, 3, 20, 0),
         seat = "C14",
         id = createUuid(3, 5),
         personId = personDtos[0].id,
         movieId = movieDtos[4].id
      )
      val ticket4Dto = TicketDto(
         dateTime = LocalDateTime(2023, 6, 4, 17, 0),
         seat = "D16",
         id = createUuid(4, 5),
         personId = personDtos[0].id,
         movieId = movieDtos[6].id
      )
      val ticket5Dto = TicketDto(
         dateTime = LocalDateTime(2023, 7, 5, 20, 0),
         seat = "E18",
         id = createUuid(5, 5),
         personId = personDtos[1].id,
         movieId = movieDtos[1].id
      )
      val ticket6Dto = TicketDto(
         dateTime = LocalDateTime(2023, 8, 6, 17, 0),
         seat = "F20",
         id = createUuid(6, 5),
         personId = personDtos[1].id,
         movieId = movieDtos[3].id
      )
      val ticket7Dto = TicketDto(
         dateTime = LocalDateTime(2023, 9, 7, 20, 0),
         seat = "G22",
         id = createUuid(7, 5),
         personId = personDtos[1].id,
         movieId = movieDtos[5].id
      )
      val ticket8Dto = TicketDto(
         dateTime = LocalDateTime(2023, 10, 8, 17, 0),
         seat = "H24",
         id = createUuid(8, 5),
         personId = personDtos[2].id,
         movieId = movieDtos[0].id
      )
      val ticket9Dto = TicketDto(
         dateTime = LocalDateTime(2023, 11, 9, 20, 0),
         seat = "I26",
         id = createUuid(9, 5),
         personId = personDtos[2].id,
         movieId = movieDtos[1].id
      )
      val ticket10Dto = TicketDto(
         dateTime = LocalDateTime(2023, 12, 10, 14, 0),
         seat = "J28",
         id = createUuid(10, 5),
         personId = personDtos[3].id,
         movieId = movieDtos[2].id
      )
      val ticket11Dto = TicketDto(
         dateTime = LocalDateTime(2023, 1, 11, 17, 0),
         seat = "K30",
         id = createUuid(11, 5),
         personId = personDtos[4].id,
         movieId = movieDtos[7].id
      )
      val ticket12Dto = TicketDto(
         dateTime = LocalDateTime(2023, 2, 12, 20, 0),
         seat = "L32",
         id = createUuid(12, 5),
         personId = personDtos[4].id,
         movieId = movieDtos[9].id
      )
      val ticket13Dto = TicketDto(
         dateTime = LocalDateTime(2023, 3, 13, 14, 0),
         seat = "M34",
         id = createUuid(13, 5),
         personId = personDtos[4].id,
         movieId = movieDtos[11].id
      )
      val ticket14Dto = TicketDto(
         dateTime = LocalDateTime(2023, 4, 14, 17, 0),
         seat = "N36",
         id = createUuid(14, 5),
         personId = personDtos[4].id,
         movieId = movieDtos[13].id
      )
      val ticket15Dto = TicketDto(
         dateTime = LocalDateTime(2023, 5, 15, 20, 0),
         seat = "O38",
         id = createUuid(15, 5),
         personId = personDtos[5].id,
         movieId = movieDtos[8].id
      )

      val ticket16Dto = TicketDto(
         dateTime = LocalDateTime(2023, 6, 16, 17, 0),
         seat = "P40",
         id = createUuid(16, 5),
         personId = personDtos[5].id,
         movieId = movieDtos[10].id
      )
      val ticket17Dto = TicketDto(
         dateTime = LocalDateTime(2023, 7, 17, 20, 0),
         seat = "Q42",
         id = createUuid(17, 5),
         personId = personDtos[5].id,
         movieId = movieDtos[12].id
      )
      val ticket18Dto = TicketDto(
         dateTime = LocalDateTime(2023, 8, 18, 17, 0),
         seat = "R44",
         id = createUuid(18, 5),
         personId = personDtos[6].id,
         movieId = movieDtos[7].id
      )
      val ticket19Dto = TicketDto(
         dateTime = LocalDateTime(2023, 9, 19, 20, 0),
         seat = "S46",
         id = createUuid(19, 5),
         personId = personDtos[6].id,
         movieId = movieDtos[8].id
      )
      val ticket20Dto = TicketDto(
         dateTime = LocalDateTime(2023, 10, 20, 17, 0),
         seat = "T48",
         id = createUuid(20, 5),
         personId = personDtos[7].id,
         movieId = movieDtos[9].id
      )

      ticketDtos.addAll( listOf(ticket1Dto, ticket2Dto, ticket3Dto, ticket4Dto,
         ticket5Dto, ticket6Dto, ticket7Dto, ticket8Dto, ticket9Dto, ticket10Dto,
         ticket11Dto, ticket12Dto, ticket13Dto, ticket14Dto, ticket15Dto, ticket16Dto,
         ticket17Dto, ticket18Dto, ticket19Dto, ticket20Dto))

      ticketDtos.forEach{
         logVerbose("<-Seed", "ticketDto: $it")
      }

      return this
   }


      fun disposeImages() {
      _imagesUrl.forEach { imageUrl ->
         logDebug("<disposeImages>", "Url $imageUrl")
         deleteFileOnStorage(imageUrl)
      }
   }
}