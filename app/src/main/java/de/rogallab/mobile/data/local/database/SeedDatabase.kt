package de.rogallab.mobile.data.local.database

import androidx.room.RoomDatabase
import de.rogallab.mobile.data.local.IAddressDao
import de.rogallab.mobile.data.local.ICarDao
import de.rogallab.mobile.data.local.IMovieDao
import de.rogallab.mobile.data.local.IPersonDao
import de.rogallab.mobile.data.local.IPersonMovieDao
import de.rogallab.mobile.data.local.ITicketDao
import de.rogallab.mobile.data.local.seed.Seed
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SeedDatabase(
   private val _database: RoomDatabase,
   private val _personDao: IPersonDao,
   private var _seed: Seed,
   private val _coroutineDispatcher: CoroutineDispatcher,
) : KoinComponent {

   suspend fun seedPerson(): Boolean =
      withContext(_coroutineDispatcher) {
         try {
            _personDao.count().let { count ->
               if (count > 0) {
                  logDebug("<-SeedDatabase", "seed: Database already seeded")
                  return@withContext false
               }
            }
            _database.clearAllTables()
            _personDao.insert(_seed.personDtos)
            return@withContext true
         } catch (e: Exception) {
            logError("<-SeedDatabase", "seed: ${e.message}")
         }
         return@withContext false
      }

   suspend fun seedAddresses() = withContext(_coroutineDispatcher) {
      val _addressDao: IAddressDao by inject()
      _seed = _seed.createAddresses()
      try {
         _addressDao.insert(_seed.addressDtos)
      } catch (e: Exception) {
         logError("<-SeedDatabase", "seed: ${e.message}")
      }
   }

   suspend fun seedCars() = withContext(_coroutineDispatcher) {
      _seed = _seed.createCars()
      val _carDao: ICarDao by inject()
      try {
         _carDao.insert(_seed.carDtos)
      } catch (e: Exception) {
         logError("<-SeedDatabase", "seed: ${e.message}")
      }
   }

   suspend fun seedMovies() {
      withContext(_coroutineDispatcher) {
         _seed = _seed.createMovies()
         val _movieDao: IMovieDao by inject()
         val _personMovieDao: IPersonMovieDao by inject()
         try {
            _movieDao.insert(_seed.movieDtos)
            _personMovieDao.insert(_seed.personMovieCrossRefs)
         } catch (e: Exception) {
            logError("<-SeedDatabase", "seed: ${e.message}")
         }
      }
   }
   suspend fun seedTickets()  = withContext(_coroutineDispatcher) {
      _seed = _seed.createTickets()
      val _ticketDao: ITicketDao by inject()
      try {
         _ticketDao.insert(_seed.ticketDtos)
      } catch (e: Exception) {
         logError("<-SeedDatabase", "seed: ${e.message}")
      }
   }
}
