package de.rogallab.mobile.data

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import de.rogallab.mobile.R
import de.rogallab.mobile.data.local.dtos.AddressDto
import de.rogallab.mobile.data.local.dtos.CarDto
import de.rogallab.mobile.data.local.dtos.MovieDto
import de.rogallab.mobile.data.local.dtos.PersonDto
import de.rogallab.mobile.data.local.dtos.PersonDtoMovieDtoCrossRef
import de.rogallab.mobile.data.local.dtos.TicketDto
import de.rogallab.mobile.data.local.io.deleteFileOnStorage
import de.rogallab.mobile.data.local.io.writeImageToStorage
import de.rogallab.mobile.domain.utilities.createUuid
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logVerbose
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
   var personMovieCrossRefs: MutableList<PersonDtoMovieDtoCrossRef> = mutableListOf()
   var ticketDtos: MutableList<TicketDto> = mutableListOf()

   fun createData() {

//--- P E O P L E -----------------------------------------------------------------------
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
            id = createUuid(index+1, 1)
         )
         logVerbose("<-Seed", "personDto: $personDto")
         personDtos.add(personDto)
      }
//--- I M A G E S -----------------------------------------------------------------------
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
//--- A D D R E S S E S ------------------------------------------------------------------
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
//--- C A R S ----------------------------------------------------------------------------
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
//--- M O V I E S ----------------------------------------------------------------------------
      val titles = mutableListOf(
         "The Godfather", "The Dark Knight", "The Lord of the Rings: The Return of the King",
         "Pulp Fiction", "Schindler's List", "Inception",
         "Forrest Gump", "Matrix", "Fight Club",
      )
      val directors = mutableListOf(
         "Francis Ford Coppola", "Christopher Nolan", "Peter Jackson",
         "Quentin Tarantino", "Steven Spielberg", "Christopher Nolan",
         "Robert Zemeckis", "Lana&Lilly Wachowski", "David Fincher"
      )
      for (i in titles.indices) {
         val movieDto = MovieDto(titles[i], directors[i],createUuid(i+1, 4))
         logVerbose("<-Seed", "movieDto: $movieDto")
         movieDtos.add(movieDto)
      }

      // create person-movie cross reference
      val personMovieCrossRef = PersonDtoMovieDtoCrossRef(personDtos[0].id, movieDtos[0].id)
      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[0].id, movieDtos[0].id))
      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[0].id, movieDtos[2].id))
      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[0].id, movieDtos[4].id))
      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[0].id, movieDtos[6].id))

      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[1].id, movieDtos[1].id))
      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[1].id, movieDtos[3].id))
      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[1].id, movieDtos[5].id))

      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[2].id, movieDtos[0].id))
      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[2].id, movieDtos[1].id))

      personMovieCrossRefs.add(PersonDtoMovieDtoCrossRef(personDtos[3].id, movieDtos[2].id))

      personMovieCrossRefs.forEach{
         logVerbose("<-Seed", "personMovieCrossRef: $it")
      }
   //--- T I C K E T S ------------------------------------------------------------------------


   }

   fun disposeImages() {
      _imagesUrl.forEach { imageUrl ->
         logDebug("<disposeImages>", "Url $imageUrl")
         deleteFileOnStorage(imageUrl)
      }
   }

}