package de.rogallab.mobile.data.local.dtos

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import de.rogallab.mobile.domain.utilities.emptyUuid
import de.rogallab.mobile.domain.utilities.newUuid

@Entity(
   tableName = "Address",
   foreignKeys = [ForeignKey(
      entity = PersonDto::class,
      parentColumns = ["id"],      // pk in Person
      childColumns = ["personId"], // fk in Address
      // delete address if person is deleted
      onDelete = ForeignKey.CASCADE
   )],
   indices = [Index("personId")]
)
data class AddressDto(
   val city: String = "",
   val postCode: String = "",
   @PrimaryKey
   val id: String = newUuid(),
   // fk: person with this address
   val personId: String? = emptyUuid(),
)
