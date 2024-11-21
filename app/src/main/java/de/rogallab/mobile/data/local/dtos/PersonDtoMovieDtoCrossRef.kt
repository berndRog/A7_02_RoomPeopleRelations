package de.rogallab.mobile.data.local.dtos

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
   tableName = "PersonMovieCrossRef",
   primaryKeys = ["personId", "movieId"],
   foreignKeys = [
      ForeignKey(
         entity = PersonDto::class,
         parentColumns = ["id"],
         childColumns = ["personId"],
         onDelete = ForeignKey.CASCADE
      ),
      ForeignKey(
         entity = MovieDto::class,
         parentColumns = ["id"],
         childColumns = ["movieId"],
         onDelete = ForeignKey.CASCADE
      )
   ],
   indices = [Index("personId"), Index("movieId")]
)
data class PersonDtoMovieDtoCrossRef(
   val personId: String,
   val movieId: String
)