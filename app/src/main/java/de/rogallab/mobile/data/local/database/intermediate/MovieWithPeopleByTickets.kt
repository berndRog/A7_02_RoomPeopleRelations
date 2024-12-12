package de.rogallab.mobile.data.local.database.intermediate

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.dtos.TicketDto

data class MovieWithPeopleByTickets(
   @Embedded
   val movieDto: MovieDto,
   @Relation(
      parentColumn = "id",
      entityColumn = "id",
      associateBy = Junction(
         value = TicketDto::class,
         parentColumn = "movieId",
         entityColumn = "personId"
      )
   )
   val peopleDtos: List<PersonDto>
)