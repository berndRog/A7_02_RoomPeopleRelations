package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.TicketDto
import de.rogallab.mobile.data.local.ITicketDao
import de.rogallab.mobile.data.mapping.toMovie
import de.rogallab.mobile.data.mapping.toTicket
import de.rogallab.mobile.data.mapping.toTicketDto
import de.rogallab.mobile.domain.IMovieRepository
import de.rogallab.mobile.domain.ITicketRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Movie
import de.rogallab.mobile.domain.entities.Ticket
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TicketRepository(
   private val _ticketDao: ITicketDao,
   private val _dispatcher: CoroutineDispatcher,
   private val _exceptionHandler: CoroutineExceptionHandler
): BaseRepository<Ticket, TicketDto, ITicketDao>(
   _dao = _ticketDao,
   _dispatcher = _dispatcher,
   _exceptionHandler = _exceptionHandler,
   transformToDto = { it.toTicketDto() }
), ITicketRepository {

   override fun selectAll(): Flow<ResultData<List<Ticket>>> = flow {
      try {
         _ticketDao.selectAll().collect { it: List<TicketDto> ->
            val movies: List<Ticket> = it.map { it.toTicket() }
            emit(ResultData.Success( movies ))
         }
      } catch (t: Throwable) {
         emit(ResultData.Error(t))
      }
   }.flowOn(_dispatcher+_exceptionHandler)

   override suspend fun findById(id: String): ResultData<Ticket?> =
      withContext(_dispatcher+_exceptionHandler) {
         return@withContext try {
            val ticket = _ticketDao.findById(id)?.toTicket()
            ResultData.Success( ticket )
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   companion object {
      private const val TAG = "<-TicketRepository"
   }
}