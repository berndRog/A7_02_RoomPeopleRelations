package de.rogallab.mobile.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.rogallab.mobile.AppStart
import de.rogallab.mobile.data.local.IAddressDao
import de.rogallab.mobile.data.local.ICarDao
import de.rogallab.mobile.data.local.IMovieDao
import de.rogallab.mobile.data.local.IPersonDao
import de.rogallab.mobile.data.local.IPersonMovieDao
import de.rogallab.mobile.data.local.ITicketDao
import de.rogallab.mobile.data.local.dtos.AddressDto
import de.rogallab.mobile.data.local.dtos.CarDto
import de.rogallab.mobile.data.local.dtos.MovieDto
import de.rogallab.mobile.data.local.dtos.PersonDto
import de.rogallab.mobile.data.local.dtos.PersonDtoMovieDtoCrossRef
import de.rogallab.mobile.data.local.dtos.TicketDto

@Database(
   entities = [
      PersonDto::class,
      AddressDto::class,
      CarDto::class,
      TicketDto::class,
      MovieDto::class,
      PersonDtoMovieDtoCrossRef::class
   ],
   version = AppStart.DATABASEVERSION,
   exportSchema = false
)
@TypeConverters(Converters.UuidConverter::class, Converters.LocalDateTimeUTCConverter::class)
abstract class AppDatabase : RoomDatabase() {
   abstract fun createPersonDao(): IPersonDao
   abstract fun createAddressDao(): IAddressDao
   abstract fun createCarDao(): ICarDao
   abstract fun createMovieDao(): IMovieDao
   abstract fun createPersonMovieDao(): IPersonMovieDao  // cross reference
   abstract fun createTicketDao(): ITicketDao
}