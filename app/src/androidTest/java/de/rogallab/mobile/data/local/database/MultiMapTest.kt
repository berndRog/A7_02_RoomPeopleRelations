package de.rogallab.mobile.data.local.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.rogallab.mobile.AppStart
import de.rogallab.mobile.data.dtos.AddressDto
import de.rogallab.mobile.data.dtos.CarDto
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.local.IPersonDao
import de.rogallab.mobile.data.local.seed.Seed
import de.rogallab.mobile.data.mapping.joinPerson
import de.rogallab.mobile.dataTestModules
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.domain.entities.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MultiMapTest: KoinTest {

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
      // when using a real database, this database will be destroyed
      ApplicationProvider
         .getApplicationContext<Context>()
         .deleteDatabase(AppStart.DATABASE_NAME+"_Test")
   }


   // -------------------------------------------------------------------------
   // Person [1] <--> [0..1} Address
   // -------------------------------------------------------------------------
   @Test
   fun testLoadPersonWithAddressToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedAddresses()
      // expected
      var person = joinPerson(_seed.personDtos[0],_seed.addressDtos[0], null, null, null)
      // Act
      val actual: Map<PersonDto, AddressDto?> = _personDao.loadPersonWithAddress(person.id)
      // Assert
      val actualPerson = joinPerson(actual.keys.first(), actual.values.first(), null, null, null)
      assertEquals(person, actualPerson)
   }

   @Test
   fun testLoadPeopleWithAddressToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedAddresses()
      // Act
      val actual: Map<PersonDto, AddressDto> = _personDao.loadPeopleWithAddress()
      // Assert
      actual.forEach { e: Map.Entry<PersonDto, AddressDto> ->
         // actual
         val actualPerson = joinPerson(e.key, e.value, null, null, null)
         // expected
         val personDto = _seed.personDtos.firstOrNull { actualPerson.id == it.id }
         assertNotNull(personDto)
         val addressDto = _seed.addressDtos.firstOrNull { actualPerson.address?.id == it.id }
         val person = joinPerson(personDto!!, addressDto, null, null, null)
         assertEquals(actualPerson, person)
      }
   }

   // -------------------------------------------------------------------------
   // Person [0..1] <--> [0..*] Car
   // -------------------------------------------------------------------------
   @Test
   fun testLoadPersonWithCarsToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedCars()
      // expected
      var person = joinPerson(_seed.personDtos[0],null, listOf(_seed.carDtos[0]), null, null)
      // Act
      val actual: Map<PersonDto,List<CarDto>> = _personDao.loadPersonWithCars(person.id)
      val actualPerson = joinPerson(actual.keys.first(), null, actual.values.first(), null, null)
      // Assert
      assertEquals(person, actualPerson)
   }

   @Test
   fun testLoadPeopleWithCarsToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedCars()
      // Act
      val actual: Map<PersonDto, List<CarDto>> = _personDao.loadPeopleWithCars()
      // Assert
      actual.map { e: Map.Entry<PersonDto, List<CarDto>> ->
         // actual
         val actualPerson = joinPerson(e.key, null, e.value, null, null)
         // expected
         val personDto = _seed.personDtos.firstOrNull { actualPerson.id == it.id }
         assertNotNull(personDto)
         val carsDto = _seed.carDtos.filter { carDto ->
            actualPerson.cars?.any { car: Car ->
               car.id == carDto.id
            } == true
         }
         val person = joinPerson(personDto!!, null, carsDto, null, null)

         assertEquals(actualPerson, person)
      }

   }

   // -------------------------------------------------------------------------
   // Person [0..*] <--> [0..*] Movie
   // -------------------------------------------------------------------------
   @Test
   fun testLoadPersonWithMoviesToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedMovies()
      // expected
      var person = joinPerson(
         personDto = _seed.personDtos[0],
         addressDto = null,
         carDtos = null,
         movieDtos = listOf(_seed.movieDtos[0], _seed.movieDtos[2],
                            _seed.movieDtos[4], _seed.movieDtos[6]),
         ticketDtos = null
      )
      // Act
      val actual: Map<PersonDto,List<MovieDto>> = _personDao.loadPersonWithMovies(person.id)
      val actualPerson = joinPerson(
         personDto = actual.keys.first(),
         addressDto = null,
         carDtos = null,
         movieDtos = actual.values.first(),
         ticketDtos = null
      )
      // Assert
      assertEquals(person, actualPerson)
   }

   @Test
   fun testLoadPeopleWithMoviesToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedCars()
      // Act
      val actual: Map<PersonDto, List<MovieDto>> = _personDao.loadPeopleWithMovies()
      // Assert
      actual.map { e: Map.Entry<PersonDto, List<MovieDto>> ->
         // actual
         val actualPerson = joinPerson(
            personDto = e.key,
            addressDto =  null,
            carDtos = null,
            movieDtos = e.value,
            ticketDtos = null
         )
         // expected
         val personDto = _seed.personDtos.firstOrNull { actualPerson.id == it.id }
         assertNotNull(personDto)
         val movieDtos = _seed.movieDtos.filter { movieDto ->
            actualPerson.movies?.any { movie: Movie ->
               movie.id == movieDto.id
            } == true
         }
         val person = joinPerson(
            personDto = personDto!!,
            addressDto = null,
            carDtos = null,
            movieDtos = movieDtos,
            ticketDtos = null
         )
         assertEquals(actualPerson, person)
      }
   }

   companion object {
      private const val TAG = "<-MultiMapTest"
   }
}