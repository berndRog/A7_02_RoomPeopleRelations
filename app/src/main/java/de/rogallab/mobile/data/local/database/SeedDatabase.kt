package de.rogallab.mobile.data.local.database

import de.rogallab.mobile.data.local.IAddressDao
import de.rogallab.mobile.data.local.ICarDao
import de.rogallab.mobile.data.local.IMovieDao
import de.rogallab.mobile.data.local.IPersonDao
import de.rogallab.mobile.data.local.IPersonMovieDao
import de.rogallab.mobile.data.local.ITicketDao
import de.rogallab.mobile.data.Seed
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SeedDatabase(
   private val _personDao: IPersonDao,
   private val _addressDao: IAddressDao,
   private val _carDao: ICarDao,
   private val _movieDao: IMovieDao,
   private val _personMovieDao: IPersonMovieDao,
   private val _ticketDao: ITicketDao,
   private val _coroutineDispatcher: CoroutineDispatcher,
   private val _seed: Seed
) {

   suspend fun seed() =
      withContext(_coroutineDispatcher) {
         try {
            _personDao.count().let { count ->
               if (count > 0) {
                  logDebug("<-SeedDatabase", "seed: Database already seeded")
                  return@withContext
               }
            }
            _seed.createData()
            _personDao.insert(_seed.personDtos)
            _addressDao.insert(_seed.addressDtos)
            _carDao.insert(_seed.carDtos)
            _movieDao.insert(_seed.movieDtos)
            _personMovieDao.insert(_seed.personMovieCrossRefs)

         } catch (e: Exception) {
            logError("<-SeedDatabase", "seed: ${e.message}")
         }
      }
   }
