package de.rogallab.mobile.data.local.database.intermediate

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.dtos.PersonMovieCrossRefDto

data class PersonWithMoviesByCrossRef(
   @Embedded
   val personDto: PersonDto,
   @Relation(
      parentColumn = "id",  // person.id
      entityColumn = "id",   // movie.id"
      associateBy = Junction(
         value = PersonMovieCrossRefDto::class,
         parentColumn = "personId",
         entityColumn = "movieId"
      )
   )
   val movieDtos: List<MovieDto>
)