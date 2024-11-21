package de.rogallab.mobile.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.rogallab.mobile.data.local.database.intermediate.MovieWithPeopleByTickets
import de.rogallab.mobile.data.local.database.intermediate.PersonWithMoviesByTickets
import de.rogallab.mobile.data.local.dtos.TicketDto


import kotlinx.coroutines.flow.Flow

@Dao
interface ITicketDao: IBaseDao<TicketDto> {

   // QUERIES ---------------------------------------------
   @Query("SELECT * FROM Ticket")
   fun selectAll(): Flow<List<TicketDto>>

   @Query("SELECT * FROM Ticket WHERE personId = :personId AND movieId = :movieId")
   suspend fun selectById(personId: String, movieId: String): TicketDto?

   @Transaction
   @Query("SELECT * FROM Person WHERE id = :personId")
   suspend fun getPersonWithMovies(personId: String): PersonWithMoviesByTickets

   @Transaction
   @Query("SELECT * FROM Movie WHERE id = :movieId")
   suspend fun getMovieWithPersons(movieId: String): MovieWithPeopleByTickets

}