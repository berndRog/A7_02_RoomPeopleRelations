package de.rogallab.mobile.data.local.database.intermediate

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.dtos.TicketDto


data class PersonWithMoviesByTickets(
   @Embedded
   val personDto: PersonDto,
   @Relation(
      parentColumn = "id",
      entityColumn = "id",
      associateBy = Junction(
         value = TicketDto::class,
         parentColumn = "personId",
         entityColumn = "movieId"
      )
   )
   val movieDtos: List<MovieDto>
)