package de.rogallab.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.rogallab.mobile.data.dtos.PersonMovieCrossRefDto

@Dao
interface IPersonMovieDao {


   @Insert(onConflict = OnConflictStrategy.ABORT)
   suspend fun insert(personMovieCrossRef: PersonMovieCrossRefDto)

   @Insert(onConflict = OnConflictStrategy.ABORT)
   suspend fun insert(peopleMoviesCrossRef: List<PersonMovieCrossRefDto>)

   @Query("DELETE FROM PersonMovieCrossRef WHERE personId = :personId AND movieId = :movieId")
   suspend fun delete(personId: String, movieId: String)

   @Query("SELECT * FROM PersonMovieCrossRef WHERE personId = :personId")
   suspend fun getMoviesForPerson(personId: String): List<PersonMovieCrossRefDto>

   @Query("SELECT * FROM PersonMovieCrossRef WHERE movieId = :movieId")
   suspend fun getPersonsForMovie(movieId: String): List<PersonMovieCrossRefDto>
}

