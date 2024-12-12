package de.rogallab.mobile.data.local.mappings

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.rogallab.mobile.data.dtos.AddressDto
import de.rogallab.mobile.data.dtos.CarDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.local.database.SeedDatabase
import de.rogallab.mobile.data.local.seed.Seed
import de.rogallab.mobile.data.mapping.toAddress
import de.rogallab.mobile.data.mapping.toCar
import de.rogallab.mobile.data.mapping.toPerson
import de.rogallab.mobile.dataTestModules
import de.rogallab.mobile.domain.entities.Address
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.domain.entities.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MappingsTest: KoinTest {

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

      _seed = get<Seed>()
      _seedDatabase = get()
   }

   @After
   fun teardown() {
      Dispatchers.resetMain()
   }

   @Test
   fun testPersonDtoToPerson() = runTest {
      // Arrange
      val personDto = _seed.personDtos[0]
      // Act
      val actualPerson = personDto.toPerson()
      // Assert
      assertPersonDtoToPerson(personDto, actualPerson)
   }

   @Test
   fun testPersonDtosToPeople() = runTest {
      // Arrange
      val personDtos = _seed.personDtos
      // Act
      val actualPeople = personDtos.map { personDto:PersonDto -> personDto.toPerson() }
      // Arrange
      assertEquals(26, actualPeople.size)
      actualPeople.forEachIndexed { index, person ->
         assertPersonDtoToPerson(personDtos[index], person)
      }
   }

   @Test
   fun testAddressDtoToAddress() = runTest {
      // Arrange
      _seed.createAddresses()
      val personDto = _seed.personDtos[0]
      val addressDto = _seed.addressDtos[0]
      // Act
      val actualPerson = personDto.toPerson()
      val actualAddress = addressDto.toAddress()
      // Assert
      assertPersonDtoToPerson(personDto, actualPerson)
      assertAddressDtoToAddress(addressDto, actualAddress)
   }
   @Test
   fun testAddressDtosToAddresses() = runTest {
      // Arrange
      _seed.createAddresses()
      val personDtos = _seed.personDtos
      val addressDtos = _seed.addressDtos
      // Act
      val actualPeople = personDtos.map { personDto:PersonDto -> personDto.toPerson() }
      val actualAddresses = addressDtos.map { addressDto:AddressDto -> addressDto.toAddress() }
      // Arrange
      assertEquals(26, actualPeople.size)
      actualPeople.forEachIndexed { index, person ->
         assertPersonDtoToPerson(personDtos[index], person)
      }
      assertEquals(10, actualAddresses.size)
      actualAddresses.forEachIndexed { index, address ->
         assertAddressDtoToAddress(addressDtos[index], address)
      }
   }

   @Test
   fun testCarDtoToCar() = runTest {
      // Arrange
      _seed.createCars()
      val personDto = _seed.personDtos[0]
      val carDto = _seed.carDtos[0]  // car[0] is owned by person[0]
      // Act
      val actualPerson = personDto.toPerson()
      val actualCar = carDto.toCar()
      // Assert
      assertPersonDtoToPerson(personDto, actualPerson)
      assertCarDto(carDto, actualCar)
   }
   @Test
   fun testCarsToCars() = runTest {
      // Arrange
      _seed.createCars()
      val personDtos = _seed.personDtos
      val carDtos = _seed.carDtos
      // Act
      val actualPeople = personDtos.map { personDto:PersonDto -> personDto.toPerson() }
      val actualCars = carDtos.map { carDto:CarDto -> carDto.toCar() }
      // Arrange
      assertEquals(26, actualPeople.size)
      actualPeople.forEachIndexed { index, person ->
         assertPersonDtoToPerson(personDtos[index], person)
      }
      assertEquals(30, actualCars.size)
      actualCars.forEachIndexed { index, car ->
         assertCarDto(carDtos[index], car)
      }
   }

   private fun assertPersonDtoToPerson(personDto: PersonDto, person: Person) {
      assertEquals(personDto.firstName, person.firstName )
      assertEquals(personDto.lastName, person.lastName)
      assertEquals(personDto.email, person.email)
      assertEquals(personDto.phone, person.phone)
      assertEquals(personDto.imagePath, person.imagePath)
      assertEquals(personDto.id, person.id)
   }

   private fun assertAddressDtoToAddress(addressDto: AddressDto, address: Address) {
      assertEquals(addressDto.city, address.city )
      assertEquals(addressDto.postCode, address.postCode)
      assertEquals(addressDto.id, address.id)
      assertEquals(addressDto.personId, address.personId)
   }

   private fun assertCarDto(carDto: CarDto, car: Car) {
      assertEquals(carDto.maker, car.maker )
      assertEquals(carDto.model, car.model)
      assertEquals(carDto.id, car.id)
      assertEquals(carDto.personId, car.personId)
   }

   companion object {
      private const val TAG = "<-PersonDaoTest"
   }
}