package de.rogallab.mobile.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.rogallab.mobile.data.local.database.intermediate.PersonWithAddress
import de.rogallab.mobile.data.local.database.intermediate.PersonWithCars
//import de.rogallab.mobile.data.local.database.intermediate.PersonWithMoviesByCrossRef
import de.rogallab.mobile.data.local.dtos.AddressDto
import de.rogallab.mobile.data.local.dtos.CarDto

import de.rogallab.mobile.data.local.dtos.PersonDto
import kotlinx.coroutines.flow.Flow

@Dao
interface IPersonDao: IBaseDao<PersonDto> {
   // QUERIES ---------------------------------------------
   @Query("SELECT * FROM Person")
   fun selectAll(): Flow<List<PersonDto>>

   @Query("SELECT * FROM Person WHERE id = :personId")
   suspend fun selectById(personId: String): PersonDto?

   @Query("SELECT COUNT(*) FROM Person")
   suspend fun count(): Int

//   // COMMANDS --------------------------------------------
//   @Insert(onConflict = OnConflictStrategy.ABORT)
//   suspend fun insert(personDto: PersonDto)
//   @Update
//   suspend fun update(personDto: PersonDto)
//   @Delete
//   suspend fun delete(personDto: PersonDto)

   // I N T E R M E D I A T E   R E T U R N S ----------------------
   @Transaction
   @Query("SELECT * FROM Person")
   fun getPeopleWithAddress(): Flow<List<PersonWithAddress>>

   @Transaction
   @Query("SELECT * FROM Person WHERE id = :personId")
   suspend fun getPersonWithAddress(personId: String): PersonWithAddress


   @Transaction
   @Query("SELECT * FROM Person")
   fun getPeopleWithCars(): Flow<List<PersonWithCars>>

   @Transaction
   @Query("SELECT * FROM Person Where id = :personId")
   suspend fun getPersonWithCars(personId: String): PersonWithCars




//   @Transaction
//   @Query("SELECT * FROM Person WHERE id = :personId")
//   suspend fun getPersonWithMovies(personId: String): PersonWithMoviesByCrossRef

//   @Transaction
//   @Query("SELECT * FROM Movie WHERE id = :movieId")
//   fun getMovieWithPersons(movieId: String): MovieWithPersons


   // M U L T I M A P   R E T U R N S ----------------------
   @Transaction
   @Query(
      "SELECT * FROM Person " +
         "LEFT JOIN Address ON Person.id = Address.personId"
   )
   suspend fun loadPeopleWithAddress(): Map<PersonDto, AddressDto>

   @Transaction
   @Query(
      "SELECT * FROM Person " +
         "LEFT JOIN Address ON Person.id = Address.personId " +
         "WHERE Person.id = :personId"
   )
   suspend fun loadPersonWithAddress(personId: String): Map<PersonDto, AddressDto>

   @Transaction
   @Query(
      "SELECT * FROM Person " +
         "LEFT JOIN Car ON Person.id = Car.personId "
   )
   suspend fun loadPeopleWithCars(): Map<PersonDto, List<CarDto>>

   @Transaction
   @Query(
      "SELECT * FROM Person " +
         "LEFT JOIN Car ON Person.id = Car.personId " +
         "WHERE Person.id = :personId"
   )
   suspend fun loadPersonWithCars(personId: String): Map<PersonDto, List<CarDto>>


}