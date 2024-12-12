package de.rogallab.mobile.data.local.database

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.rogallab.mobile.AppStart
import de.rogallab.mobile.data.dtos.CarDto
import de.rogallab.mobile.data.local.IPersonDao
import de.rogallab.mobile.data.local.database.intermediate.PersonWithAddress
import de.rogallab.mobile.data.local.database.intermediate.PersonWithCars
import de.rogallab.mobile.data.local.seed.Seed
import de.rogallab.mobile.data.mapping.joinPerson
import de.rogallab.mobile.data.mapping.toPersonWithAddress
import de.rogallab.mobile.data.mapping.toPersonWithCars
import de.rogallab.mobile.dataTestModules
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class IntermediateTest: KoinTest {

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
   @Test  // with intermediate classes
   fun testGetPersonWithAddress() = runTest {
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

   @Test  // with intermediate classes
   fun testGetPersonWithAddressToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedAddresses()
      // expected
      var person = joinPerson(
         personDto =  _seed.personDtos[0],
         addressDto = _seed.addressDtos[0],
         carDtos = null,
         movieDtos = null,
         ticketDtos = null
      )
      // Act
      val actualPersonWithAddress = _personDao.getPersonWithAddress(person.id)
      val actualPerson = actualPersonWithAddress.toPersonWithAddress()
      // Assert
      assertEquals(actualPerson, person)
   }

   @Test  // intermediate class
   fun testGetPeopleWithAddressToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedAddresses()
      // Act
      val actual: List<PersonWithAddress> = _personDao.getPeopleWithAddress().first()
      // Assert
      actual.forEach{ actualPersonWithAddress ->
         // actual
         val actualPerson = actualPersonWithAddress.toPersonWithAddress()
         // expected
         val personDto = _seed.personDtos.firstOrNull { actualPerson.id == it.id }
         assertNotNull(personDto)
         val addressDto = _seed.addressDtos.firstOrNull { actualPerson.address?.id == it.id }
         val person = joinPerson(
            personDto = personDto!!,
            addressDto = addressDto,
            carDtos = null,
            movieDtos = null,
            ticketDtos = null
         )
         assertEquals(actualPerson, person)
      }
   }

   // -------------------------------------------------------------------------
   // Person [0..1] <--> [0..*] Cars
   // -------------------------------------------------------------------------
   @Test
   fun testPersonWithCarsToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedCars()
      // expected
      var person = joinPerson(
         personDto = _seed.personDtos[0],
         addressDto =  null,
         carDtos =  listOf(_seed.carDtos[0]),
         movieDtos = null,
         ticketDtos = null
      )
      // Act
      val actual: PersonWithCars = _personDao.getPersonWithCars(person.id)
      val actualPerson = joinPerson(actual.personDto, null, actual.carDtos, null, null)
      // Assert
      assertEquals(person, actualPerson)
   }

   @Test
   fun testPeopleWithCarsToPerson() = runTest {
      // Arrange
      _seedDatabase.seedPerson()
      _seedDatabase.seedCars()
      // Act
      val actual: List<PersonWithCars> = _personDao.getPeopleWithCars().first()
      // Assert
      actual.forEach{ personWithCars ->
         // actual
         val actualPerson = personWithCars.toPersonWithCars()
         // expected
         val personDto = _seed.personDtos.firstOrNull { it.id == actualPerson.id }
         assertNotNull(personDto)
         val carDtos = mutableListOf<CarDto>()
         actualPerson.cars.forEach{ actualCar ->
            _seed.carDtos.firstOrNull { it.id == actualCar.id }?.let { carDto ->
               carDtos.add(carDto)
            }
         }
         val person = joinPerson(
            personDto = personDto!!,
            addressDto = null,
            carDtos = carDtos,
            movieDtos = null,
            ticketDtos = null
         )
         assertEquals(person, actualPerson)
      }
   }

   companion object {
      private const val TAG = "<-IntermediateTest"
   }
}