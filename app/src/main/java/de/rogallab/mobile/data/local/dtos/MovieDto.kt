package de.rogallab.mobile.data.local.dtos

import androidx.room.Entity
import androidx.room.PrimaryKey
import de.rogallab.mobile.domain.utilities.newUuid


@Entity(
   tableName="Movie"
)
data class MovieDto(
   val title: String = "",
   val director: String = "",
   @PrimaryKey
   val id: String = newUuid(),
)