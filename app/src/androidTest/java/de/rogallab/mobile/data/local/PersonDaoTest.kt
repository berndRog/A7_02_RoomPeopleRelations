package de.rogallab.mobile.data.local

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.rogallab.mobile.data.local.database.AppDatabase
import de.rogallab.mobile.data.local.database.SeedDatabase
import de.rogallab.mobile.data.local.database.intermediate.PersonWithAddress
import de.rogallab.mobile.data.local.database.intermediate.PersonWithCars
import de.rogallab.mobile.data.local.database.intermediate.PersonWithMoviesByCrossRef
import de.rogallab.mobile.data.dtos.AddressDto
import de.rogallab.mobile.data.dtos.CarDto
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.dtos.TicketDto
import de.rogallab.mobile.data.local.seed.Seed
import de.rogallab.mobile.dataTestModules
import de.rogallab.mobile.domain.utilities.logDebug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.get
import org.koin.core.logger.Level
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PersonDaoTest: KoinTest {

   private lateinit var _database: AppDatabase
   private lateinit var _personDao: IPersonDao
   private lateinit var _seed: Seed
   private lateinit var _seedDatabase: SeedDatabase

   @get:Rule
   val koinTestRule = KoinTestRule.create {
      androidLogger(Level.ERROR) // Optional
      androidContext(ApplicationProvider.getApplicationContext())
      modules(dataTestModules)
   }


   @Before
   fun setUp() {
      //
      val standardTestDispatcher = get<TestDispatcher>()
      Dispatchers.setMain(standardTestDispatcher)

      _database = get<AppDatabase>()
      _personDao = get<IPersonDao>()
      _seed = get<Seed>()
      _seedDatabase = get()
   }

   @After
   fun teardown() {
      Dispatchers.resetMain()
      _database.close()
   }

   @Test
   fun testSelectById() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      val personDto = _seed.personDtos[0]
      // Act
      val actual = _personDao.selectById(personDto.id)
      // Assert
      assertEquals(actual, personDto)
   }
   @Test
   fun testSelectAll() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      // Act
      val actual = _personDao.selectAll().first()
      // Arrange
      assertEquals(26, actual.size)
      assertEquals(_seed.personDtos, actual)

   }
   @Test
   fun testInsert() = runTest {
      // Arrange
      val personDto = _seed.personDtos[0]
      // Act
      _personDao.insert(personDto)
      // Assert
      val actual = _personDao.selectById(personDto.id)
      assertEquals(actual, personDto)
   }
   @Test
   fun testUpdate() = runTest {
      // Arrange
      val personDto = _seed.personDtos[0]
      _personDao.insert(personDto)
      // Act
      val updatedPersonDto = personDto.copy(firstName = "Arne updated", lastName = "Arndt updated")
      _personDao.update(updatedPersonDto)
      // Assert
      val actual = _personDao.selectById(personDto.id)
      assertEquals(actual, updatedPersonDto)
   }
   @Test
   fun testDelete() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      val personDto = _seed.personDtos[0]
      // Act
      _personDao.delete(personDto)
      // Assert
      val actual = _personDao.selectById(personDto.id)
      assertNull(actual)
   }
   // -------------------------------------------------------------------------
   // Person [1] <--> [0..1} Address
   // -------------------------------------------------------------------------
   @Test  // with intermediate classes
   fun testPeopleWithAddress() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedAddresses()
      // Act
      val actual: List<PersonWithAddress> = _personDao.getPeopleWithAddress().first()
      val actualPersonDtos: List<PersonDto> = actual.map {  it.personDto }
      val actualAddressDtos: List<AddressDto> = actual.mapNotNull { it.addressDto }
      // Assert
      assertEquals(_seed.personDtos, actualPersonDtos)
      assertEquals(_seed.addressDtos, actualAddressDtos)
   }
   @Test  // intermediate class
   fun testPersonWithAddress() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedAddresses()
      val personDto = _seed.personDtos[0]
      val addressDto = _seed.addressDtos[0]
      // Act
      val actual: PersonWithAddress = _personDao.getPersonWithAddress(personDto.id)
      // Assert
      assertEquals(personDto, actual.personDto)
      assertEquals(addressDto, actual.addressDto)
   }
   @Test  // multimap return
   fun testPeopleWithAddressMultiMap() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedAddresses()
      // Act
      val actual: Map<PersonDto, AddressDto> =
         _personDao.loadPeopleWithAddress()
      val actualPersonDtos: List<PersonDto> = actual.keys.mapNotNull { it }
      val actualAddressDtos: List<AddressDto> = actual.values.mapNotNull { it }
      // Assert
      assertEquals(_seed.personDtos, actualPersonDtos)
      assertEquals(_seed.addressDtos, actualAddressDtos)
   }
   @Test  // multimap return
   fun testPersonWithAddressMultiMap() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedAddresses()
      val personDto = _seed.personDtos[0]
      val addressDto = _seed.addressDtos[0]
      // Act
      val actual:Map<PersonDto, AddressDto?> =
         _personDao.loadPersonWithAddress(personDto.id)

      //actual.map { it.keys.first() }.toList()

      val actualPersonDto: PersonDto = actual.keys.first()
      val actualAddressDto: AddressDto? = actual.values.first()
      // Assert
      assertEquals(personDto, actualPersonDto)
      assertEquals(addressDto, actualAddressDto)
   }

   // -------------------------------------------------------------------------
   // Person [0..1] <--> [0..*] Cars
   // -------------------------------------------------------------------------
   @Test  // with intermediate classes
   fun testPeopleWithCars() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedCars()
      val personDtos = _seed.personDtos
      // only cars which are owned by a person
      val carsDtos = _seed.carDtos.filter { it.personId != null }
      // Act
      val actual: List<PersonWithCars> = _personDao.getPeopleWithCars().first()
      val actualPersonDtos: List<PersonDto> = actual.map {  it.personDto }
      val actualCarDtos: List<CarDto> = actual.flatMap { it.carDtos }
      // Assert
      assertEquals(personDtos, actualPersonDtos)
      assertEquals(carsDtos, actualCarDtos)
   }
   @Test  // intermediate class
   fun testPersonWithCars() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedCars()
      val personDto = _seed.personDtos[0]
      // only cars which are owned by personDto
      val carDtos = _seed.carDtos.filter { it.personId == personDto.id }
      // Act
      val actual: PersonWithCars = _personDao.getPersonWithCars(personDto.id)
      // Assert
      assertEquals(personDto, actual.personDto)
      assertEquals(carDtos, actual.carDtos)
   }
   @Test  // multimap return
   fun testPeopleWithCarsMultiMap() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedCars()
      val personDtos = _seed.personDtos
      // only cars which are owned by a person
      val carDtos = _seed.carDtos.filter { it.personId != null }
      // Act
      val actual: Map<PersonDto, List<CarDto>> =
         _personDao.loadPeopleWithCars()
      val actualPersonDtos: List<PersonDto> = actual.keys.map { it }
      val actualCarDtos: List<CarDto> = actual.values.flatten()  // simplified flatMap {}
      // Assert
      assertEquals(personDtos, actualPersonDtos)
      assertEquals(carDtos, actualCarDtos)
   }
   @Test  // multimap return
   fun testPersonWithCarsMultiMap() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedCars()
      val personDto = _seed.personDtos[0]
      // only cars which are owned by personDto
      val carDtos = _seed.carDtos.filter { it.personId == personDto.id }
      // Act
      val actual:Map<PersonDto, List<CarDto>> =
         _personDao.loadPersonWithCars(personDto.id)
      val actualPersonDto: PersonDto = actual.keys.first()
      val actualCarDtos: List<CarDto> = actual.values.first()
      // Assert
      assertEquals(personDto, actualPersonDto)
      assertEquals(carDtos, actualCarDtos)
   }

   // -------------------------------------------------------------------------
   // Person [0..*] <--> [0..*] Movies
   // -------------------------------------------------------------------------
   @Test  // with intermediate classes
   fun testPeopleWithMovies() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedMovies()
      val personDtos = _seed.personDtos
      // only cars which are owned by a person
      // val carsDtos = _seed.carDtos.filter { it.personId != null }
      // Act
      val actual: List<PersonWithCars> = _personDao.getPeopleWithCars().first()
      val actualPersonDtos: List<PersonDto> = actual.map {  it.personDto }
      val actualCarDtos: List<CarDto> = actual.flatMap { it.carDtos }
      // Assert
      assertEquals(personDtos, actualPersonDtos)
      //assertEquals(carsDtos, actualCarDtos)
   }
   @Test  // intermediate class
   fun testPersonWithMovies() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedMovies()
      val personDto = _seed.personDtos[0]
      logDebug(TAG, "personDto: $personDto")
      val movieIds: List<String> = _seed.personMovieCrossRefs
            .filter { crossRef -> crossRef.personId == personDto.id }
            .map { crossRef -> crossRef.movieId }
      logDebug(TAG, "movieIds: $movieIds")
      val movieDtosForPerson: List<MovieDto> = _seed.movieDtos
            .filter { movieDto -> movieDto.id in movieIds }
      logDebug(TAG, "movieDtosForPerson: $movieDtosForPerson")
      // Act
      val actual: PersonWithMoviesByCrossRef = _personDao.getPersonWithMovies(personDto.id)
      val actualPersonDto = actual.personDto
      val actualMovieDtos = actual.movieDtos
      // Assert
      assertEquals(personDto, actualPersonDto)
      assertEquals(movieDtosForPerson, actualMovieDtos)
   }

   @Test  // with intermediate classes
   fun testPeopleWithMoviesMultiMap() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedMovies()
      val personDtos = _seed.personDtos
      val movieDtos = _seed.movieDtos
      // Act
      val actual: Map<PersonDto,List<MovieDto>> = _personDao.loadPeopleWithMovies()
      val actualPersonDtos: List<PersonDto> = actual.keys.toList()
      val actualMovieDtos: List<MovieDto> = actual.values.flatten()  // personDto -> List<MovieDto> to global li
      // Assert
      actualPersonDtos.forEach { actualPersonDto ->
         val personDto = personDtos.find { it.id == actualPersonDto.id }
         assertEquals(personDto, actualPersonDto)
      }

      actualMovieDtos.forEach { actualMovieDto ->
         val movieDto = movieDtos.find { it.id == actualMovieDto.id }
         assertEquals(movieDto, actualMovieDto)
      }
   }

   @Test  // multimap return
   fun testPersonWithMoviesMultiMap() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedMovies()
      val personDto = _seed.personDtos[0]
      // only movies which were visited by personDto
      val movieIds: List<String> = _seed.personMovieCrossRefs
         .filter { crossRef -> crossRef.personId == personDto.id }
         .map { crossRef -> crossRef.movieId }
      val movieDtosForPerson: List<MovieDto> = _seed.movieDtos
         .filter { movieDto -> movieDto.id in movieIds }
      // Act
      val actual:Map<PersonDto, List<MovieDto>> =
         _personDao.loadPersonWithMovies(personDto.id)
      val actualPersonDto: PersonDto = actual.keys.first()
      val actualMovieDtos: List<MovieDto> = actual.values.first()
      // Assert
      assertEquals(personDto, actualPersonDto)
      assertEquals(movieDtosForPerson, actualMovieDtos)
   }

   // -------------------------------------------------------------------------
   // Person [1] <--> [0..*] Ticket [0..*] <--> [1] Movie
   // -------------------------------------------------------------------------
   @Test
   fun testPersonWithTickets() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedMovies()
      _seedDatabase.seedTickets()
      val personDto = _seed.personDtos[0]
      val ticketDtosForPerson = _seed.ticketDtos
         .filter { ticketDto -> ticketDto.personId == personDto.id }
      // Act
      val actual: Map<PersonDto, List<TicketDto>> =
         _personDao.loadPersonWithTickets(personDto.id)
      val actualPersonDto: PersonDto = actual.keys.first()
      val actualTicketDtos: List<TicketDto> = actual.values.first()
      // Assert
      assertEquals(personDto, actualPersonDto)
      assertEquals(ticketDtosForPerson, actualTicketDtos)
   }

   @Test
   fun testPersonWithMoviesViaTickets() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedMovies()
      _seedDatabase.seedTickets()
      val personDto = _seed.personDtos[0]
      val movieIds: List<String> = _seed.ticketDtos
         .filter { ticketDto -> ticketDto.personId == personDto.id }
         .map { tickets -> tickets.movieId }
      val movieDtosForPerson: List<MovieDto> = _seed.movieDtos
         .filter { movieDto -> movieDto.id in movieIds }
      // Act
      val actual: Map<PersonDto, List<MovieDto>> =
         _personDao.loadPersonWithMoviesViaTickets(personDto.id)
      val actualPersonDto: PersonDto = actual.keys.first()
      val actualMovieDtos: List<MovieDto> = actual.values.first()
      // Assert
      assertEquals(personDto, actualPersonDto)
      assertEquals(movieDtosForPerson, actualMovieDtos)
   }


   companion object {
      private const val TAG = "<-PersonDaoTest"
   }


}