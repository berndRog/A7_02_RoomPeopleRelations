package de.rogallab.mobile.domain.entities

import de.rogallab.mobile.domain.utilities.newUuid

data class Car(
   val maker: String = "",
   val model: String = "",
   val id: String = newUuid(),
   // Car -> [0..1] Person
   val person: Person? = null,
   // fk: owner of the car
   val personId: String? = null
)
