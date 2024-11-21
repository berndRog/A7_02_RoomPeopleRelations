package de.rogallab.mobile.data.local.database.intermediate

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.rogallab.mobile.data.local.dtos.MovieDto
import de.rogallab.mobile.data.local.dtos.PersonDto
import de.rogallab.mobile.data.local.dtos.PersonDtoMovieDtoCrossRef

data class PersonWithMoviesByCrossRef(
   @Embedded
   val personDto: PersonDto,
   @Relation(
      parentColumn = "id",      // person.id
      entityColumn = "movieId", //
      associateBy = Junction(PersonDtoMovieDtoCrossRef::class)
   )
   val movieDtos: List<MovieDto>
)