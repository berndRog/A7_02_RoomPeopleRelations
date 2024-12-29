package de.rogallab.mobile.domain.entities

import de.rogallab.mobile.domain.utilities.newUuid

data class Person (
   val firstName: String = "",
   val lastName: String = "",
   val email: String? = null,
   val phone: String? = null,
   val imagePath: String? = null,
   val id: String,  // Uuid as String

   // Relations to other domainModel classes
   // Person ->  [0..1] Address
   val address: Address? = null,
   // Person ->  [0..*] Car
   val cars: MutableList<Car> = mutableListOf(),
   // Person ->  [0..*] Movie
   val movies: MutableList<Movie> = mutableListOf(),
   // Person: -> [0..*] Ticket
   val tickets: MutableList<Ticket> = mutableListOf()
)