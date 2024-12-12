package de.rogallab.mobile.data.local

import androidx.room.Dao
import androidx.room.Query
import de.rogallab.mobile.data.dtos.CarDto

import kotlinx.coroutines.flow.Flow

@Dao
interface ICarDao: IBaseDao<CarDto> {
   // QUERIES ---------------------------------------------
   @Query("SELECT * FROM Car")
   fun selectAll(): Flow<List<CarDto>>

   @Query("SELECT * FROM Car WHERE id = :id")
   suspend fun findById(id: String): CarDto?
}