package de.rogallab.mobile.data.local.dtos

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import de.rogallab.mobile.domain.utilities.newUuid

@Entity(
   tableName = "Car",
   foreignKeys = [ForeignKey(
      entity = PersonDto::class,
      parentColumns = ["id"],
      childColumns = ["personId"],
      onDelete = ForeignKey.SET_NULL
   )],
   indices = [Index("personId")]
)
data class CarDto(
   val maker: String = "",
   val model: String = "",
   @PrimaryKey
   val id: String = newUuid(),
   // fk: owner of the car
   val personId: String? = null
)
