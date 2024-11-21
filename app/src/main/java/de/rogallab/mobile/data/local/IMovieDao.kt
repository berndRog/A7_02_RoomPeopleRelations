package de.rogallab.mobile.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.rogallab.mobile.data.local.database.intermediate.MovieWithPeopleByTickets
import de.rogallab.mobile.data.local.dtos.MovieDto
import kotlinx.coroutines.flow.Flow

@Dao
interface IMovieDao: IBaseDao<MovieDto> {
   // QUERIES ---------------------------------------------
   @Query("SELECT * FROM Movie")
   fun selectAll(): Flow<List<MovieDto>>

   @Query("SELECT * FROM Movie WHERE id = :id")
   suspend fun selectById(id: String): MovieDto?

   @Query("SELECT COUNT(*) FROM Movie")
   suspend fun count(): Int

   @Transaction
   @Query("SELECT * FROM Movie WHERE id = :movieId")
   suspend fun getMovieWithPersons(movieId: String): MovieWithPeopleByTickets

}