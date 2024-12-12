package de.rogallab.mobile.data.local.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.dtos.TicketDto

data class PersonWithTickets(
   @Embedded val personDto: PersonDto,
   @Relation(
      parentColumn = "id",
      entityColumn = "personId"
   )
   val ticketDtos: List<TicketDto>
)