package de.rogallab.mobile.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.rogallab.mobile.data.local.database.intermediate.MovieWithPeopleByTickets
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import kotlinx.coroutines.flow.Flow

@Dao
interface IMovieDao: IBaseDao<MovieDto> {
   // QUERIES ---------------------------------------------
   @Query("SELECT * FROM Movie")
   fun selectAll(): Flow<List<MovieDto>>

   @Query("SELECT * FROM Movie WHERE id = :id")
   suspend fun findById(id: String): MovieDto?

   @Transaction
   @Query("SELECT * FROM Movie WHERE id = :movieId")
   suspend fun getMovieWithPersons(movieId: String): MovieWithPeopleByTickets


   // M U L T I M A P   R E T U R N S ----------------------
   @Transaction
   @Query(
      "SELECT Movie.*, Person.* FROM Movie " +
         "INNER JOIN PersonMovieCrossRef ON Movie.id = PersonMovieCrossRef.movieId " +
         "INNER JOIN Person ON PersonMovieCrossRef.personId = Person.id"
   )
   suspend fun loadMoviesWithPeople(): Map<MovieDto, List<PersonDto>>

   @Transaction
   @Query(
      "SELECT Movie.*, Person.* FROM Movie " +
         "INNER JOIN PersonMovieCrossRef ON Movie.id = PersonMovieCrossRef.movieId " +
         "INNER JOIN Person ON PersonMovieCrossRef.personId = Person.id " +
         "WHERE Movie.id = :movieId"
   )
   suspend fun loadMovieWithPeople(movieId: String): Map<MovieDto, List<PersonDto>>

}