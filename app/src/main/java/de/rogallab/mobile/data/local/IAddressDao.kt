package de.rogallab.mobile.data.local

import androidx.room.Dao
import androidx.room.Query
import de.rogallab.mobile.data.local.dtos.AddressDto
import kotlinx.coroutines.flow.Flow

@Dao
interface IAddressDao: IBaseDao<AddressDto> {
   // QUERIES ---------------------------------------------
   @Query("SELECT * FROM Address")
   fun selectAll(): Flow<List<AddressDto>>

   @Query("SELECT * FROM Address WHERE id = :id")
   suspend fun selectById(id: String): AddressDto?
}