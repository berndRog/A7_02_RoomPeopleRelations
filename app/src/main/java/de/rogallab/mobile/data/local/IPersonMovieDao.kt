package de.rogallab.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.rogallab.mobile.data.local.dtos.PersonDtoMovieDtoCrossRef

@Dao
interface IPersonMovieDao {


   @Insert(onConflict = OnConflictStrategy.ABORT)
   suspend fun insert(personMovieCrossRef: PersonDtoMovieDtoCrossRef)

   @Insert(onConflict = OnConflictStrategy.ABORT)
   suspend fun insert(peopleMoviesCrossRef: List<PersonDtoMovieDtoCrossRef>)

   @Query("DELETE FROM PersonMovieCrossRef WHERE personId = :personId AND movieId = :movieId")
   suspend fun delete(personId: String, movieId: String)

   @Query("SELECT * FROM PersonMovieCrossRef WHERE personId = :personId")
   suspend fun getMoviesForPerson(personId: String): List<PersonDtoMovieDtoCrossRef>

   @Query("SELECT * FROM PersonMovieCrossRef WHERE movieId = :movieId")
   suspend fun getPersonsForMovie(movieId: String): List<PersonDtoMovieDtoCrossRef>
}

