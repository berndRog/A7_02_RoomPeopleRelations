package de.rogallab.mobile.data.local.database.intermediate

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.dtos.PersonMovieCrossRefDto

data class MovieWithPeopleByCrossRef(
   @Embedded
   val movieDto: MovieDto,
   @Relation(
      parentColumn = "id",
      entityColumn = "personId",
      associateBy = Junction(PersonMovieCrossRefDto::class)
   )
   val peopleDtos: List<PersonDto>
)