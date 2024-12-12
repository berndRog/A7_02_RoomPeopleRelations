package de.rogallab.mobile.data.dtos

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.rogallab.mobile.domain.utilities.newUuid

@Entity(
   tableName="Movie"
)
data class MovieDto(
   val title: String = "",
   val director: String = "",
   val year: Int = 0,
   @PrimaryKey
   val id: String = newUuid(),
)