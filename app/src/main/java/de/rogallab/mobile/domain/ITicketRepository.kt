package de.rogallab.mobile.domain
import de.rogallab.mobile.domain.entities.Ticket
import kotlinx.coroutines.flow.Flow

interface ITicketRepository: IBaseRepository<Ticket> {
    fun selectAll(): Flow<ResultData<List<Ticket>>>
    suspend fun findById(id: String): ResultData<Ticket?>
}