package de.rogallab.mobile.data.local.database.intermediate

import androidx.room.Embedded
import androidx.room.Relation
import de.rogallab.mobile.data.local.dtos.CarDto
import de.rogallab.mobile.data.local.dtos.PersonDto

// One-to-Many
data class PersonWithCars(
   @Embedded
   val personDto: PersonDto,
   @Relation(  // person.id == car.personId
      parentColumn = "id",
      entityColumn = "personId"
   )
   val carDtos: List<CarDto>
)
