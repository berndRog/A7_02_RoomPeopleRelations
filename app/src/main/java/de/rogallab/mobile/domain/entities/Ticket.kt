package de.rogallab.mobile.domain.entities

import de.rogallab.mobile.domain.utilities.newUuid
import kotlinx.datetime.LocalDateTime

data class Ticket(
   val dateTime: LocalDateTime,
   val seat: String,
   val id: String = newUuid(),
   val personId: String, // Foreign key for Person
   val movieId: String   // Foreign key for Movie
)
