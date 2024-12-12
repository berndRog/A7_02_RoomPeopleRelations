package de.rogallab.mobile.data.local.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.TicketDto

data class MovieWithTickets(
   @Embedded
   val movieDto: MovieDto,
   @Relation(
      parentColumn = "id",
      entityColumn = "movieId"
   )
   val ticketDtos: List<TicketDto>
)