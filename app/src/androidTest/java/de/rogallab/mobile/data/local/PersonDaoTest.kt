package de.rogallab.mobile.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.rogallab.mobile.data.Seed
import de.rogallab.mobile.data.local.database.AppDatabase
import de.rogallab.mobile.data.local.database.SeedDatabase
import de.rogallab.mobile.data.local.database.intermediate.PersonWithAddress
import de.rogallab.mobile.data.local.dtos.AddressDto
import de.rogallab.mobile.data.local.dtos.PersonDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class PersonDaoTest {

   private lateinit var db: AppDatabase
   private lateinit var personDao: IPersonDao
   private lateinit var addressDao: IAddressDao
   private lateinit var carDao: ICarDao
   private lateinit var ticketDao: ITicketDao
   private lateinit var movieDao: IMovieDao
   private lateinit var personMovieDao: IPersonMovieDao
   private lateinit var seed: Seed
   private lateinit var seedDatabase: SeedDatabase

   @Before
   fun createDb() {
      val context = ApplicationProvider.getApplicationContext<Context>()
      db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
      personDao = db.createPersonDao()
      addressDao = db.createAddressDao()
      carDao = db.createCarDao()
      movieDao = db.createMovieDao()
      personMovieDao = db.createPersonMovieDao()
      ticketDao = db.createTicketDao()
      seed = Seed(context, context.resources)
      seedDatabase = SeedDatabase(
            personDao,
            addressDao,
            carDao,
            movieDao,
            personMovieDao,
            ticketDao,
            Dispatchers.Unconfined,
            seed
         )
   }

   @After
   fun closeDb() {
      seed.disposeImages()
      db.close()
   }

   @Test
   fun testSelectById() = runBlocking {
      // Arrange
      seedDatabase.seed()
      val personDto = seed.personDtos[0]
      // Act
      val actual = personDao.selectById(personDto.id)
      // Assert
      assertEquals(actual, personDto)
   }

   @Test
   fun testSelectAll() = runBlocking {
      // Arrange
      seedDatabase.seed()
      val peopleDtos = seed.personDtos
      // Act
      val actual = personDao.selectAll().first()
      // Arrange
      assertEquals(26, actual.size)
      assertEquals(actual, peopleDtos)

   }

   @Test
   fun testDelete() = runBlocking {
      val person1Dto = PersonDto(id = "1", firstName = "Arne", lastName =  "Arndt")
      personDao.insert(person1Dto)
      personDao.delete(person1Dto)

      val actual = personDao.selectById("1")
      assertNull(actual)
   }


   @Test
   fun testPeopleWithAddress() = runBlocking {
      // Arrange
      seedDatabase.seed()
      val peopleDtos = seed.personDtos
      val addressesDtos = seed.addressDtos
      // Act
      val actual: List<PersonWithAddress> = personDao.getPeopleWithAddress().first()
      val actualPersonDtos: List<PersonDto> = actual.map {  it.personDto }
      val actualAddressDtos: List<AddressDto> = actual.mapNotNull { it.addressDto }
      // Assert
      assertEquals(peopleDtos, actualPersonDtos)
      assertEquals(addressesDtos, actualAddressDtos)
   }

   @Test
   fun testPersonWithAddress() = runBlocking {
      // Arrange
      seedDatabase.seed()
      val person1Dto = seed.personDtos[0]
      val address1Dto = seed.addressDtos[0]
      // Act
      val actual: PersonWithAddress = personDao.getPersonWithAddress(person1Dto.id)
      // Assert
      assertEquals(person1Dto, actual.personDto)
      assertEquals(address1Dto, actual.addressDto)
   }


   @Test
   fun testPeopleWithAddressMultiMap() = runBlocking {
      // Arrange
      seedDatabase.seed()
      // Act
      val actual = personDao.loadPeopleWithAddress()
      val actualPersonDtos = actual.keys.toList()
      val actualAddressDtos = actual.values.toList()
      // Assert
      assertEquals(seed.personDtos, actualPersonDtos)
      assertEquals(seed.addressDtos, actualAddressDtos)
   }

   @Test
   fun testPersonWithAddressMultiMap() = runBlocking {
      // Arrange
      seedDatabase.seed()
      val person1Dto = seed.personDtos[0]
      val address1Dto = seed.addressDtos[0]
      // Act
      val actual = personDao.loadPersonWithAddress(person1Dto.id)
      //actual.map { it.keys.first() }.toList()

      val actualPersonDto: PersonDto = actual.keys.first()
      val actualAddressDto: AddressDto = actual.values.first()
      // Assert
      assertEquals(person1Dto, actualPersonDto)
      assertEquals(address1Dto, actualAddressDto)
   }


}