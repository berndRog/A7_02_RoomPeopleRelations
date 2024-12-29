package de.rogallab.mobile.domain.entities

import de.rogallab.mobile.domain.utilities.newUuid

data class Movie(
   val title: String = "",
   val director: String = "",
   val year: Int = 0,
   val id: String,

   // Movie: -> [0..*] Ticket
   val tickets: MutableList<Ticket> = mutableListOf()
)