package de.rogallab.mobile.domain.entities

import kotlinx.datetime.LocalDateTime

data class Ticket(
   val dateTime: LocalDateTime,
   val seat: String,
   val id: String,
   val personId: String, // Foreign key for Person
   val movieId: String   // Foreign key for Movie
)
