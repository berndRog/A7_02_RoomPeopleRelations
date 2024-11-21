package de.rogallab.mobile.data.local.dtos

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import de.rogallab.mobile.domain.utilities.newUuid
import kotlinx.datetime.LocalDateTime

@Entity(
   tableName = "Ticket",
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
data class TicketDto(
   val dateTime: LocalDateTime,
   val seat: String,
   val price: Double,
   val id: String = newUuid(),
   val personId: String, // Foreign key for Person
   val movieId: String   // Foreign key for Movie
)
