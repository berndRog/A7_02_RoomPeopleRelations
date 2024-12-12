package de.rogallab.mobile.data.local.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import de.rogallab.mobile.data.dtos.AddressDto
import de.rogallab.mobile.data.dtos.PersonDto

// One-to-One
data class PersonWithAddress(
   @Embedded
   val personDto: PersonDto,
   @Relation(  // person.id == address.userId
      parentColumn = "id",
      entityColumn = "personId"
   )
   val addressDto: AddressDto?
)

