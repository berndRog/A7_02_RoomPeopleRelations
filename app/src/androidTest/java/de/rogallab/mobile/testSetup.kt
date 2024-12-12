package de.rogallab.mobile

import androidx.room.Room
import de.rogallab.mobile.data.local.IAddressDao
import de.rogallab.mobile.data.local.ICarDao
import de.rogallab.mobile.data.local.IMovieDao
import de.rogallab.mobile.data.local.IPersonDao
import de.rogallab.mobile.data.local.IPersonMovieDao
import de.rogallab.mobile.data.local.ITicketDao
import de.rogallab.mobile.data.local.database.AppDatabase
import de.rogallab.mobile.data.local.database.SeedDatabase
import de.rogallab.mobile.data.local.seed.Seed
import de.rogallab.mobile.data.repositories.AddressRepository
import de.rogallab.mobile.data.repositories.CarRepository
import de.rogallab.mobile.data.repositories.MovieRepository
import de.rogallab.mobile.data.repositories.PersonRepository
import de.rogallab.mobile.domain.IAddressRepository
import de.rogallab.mobile.domain.ICarRepository
import de.rogallab.mobile.domain.IMovieRepository
import de.rogallab.mobile.domain.IPersonRepository
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.domain.utilities.logInfo
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataTestModules = module {
   val tag = "<-dataTestModules"

   logInfo(tag, "single    -> CoroutineExceptionHandler")
   single<CoroutineExceptionHandler> {
      CoroutineExceptionHandler { _, exception ->
         logError(tag, "Coroutine exception: ${exception.localizedMessage}")
      }
   }

   logInfo(tag, "single    -> TestCoroutineScheduler")
   single<TestCoroutineScheduler> {
      TestCoroutineScheduler().apply {  this.advanceUntilIdle() }
   }
   logInfo(tag, "single    -> TestDispatcher")
   single<TestDispatcher> {
      StandardTestDispatcher( get<TestCoroutineScheduler>())
   }
   logInfo(tag, "single    -> CoroutineScope")
   single<CoroutineScope> {
      CoroutineScope(SupervisorJob() + get<TestDispatcher>())
   }

   logInfo(tag, "single    -> Seed")
   single<Seed> {
      Seed(
         androidContext(),
         androidContext().resources
      ).createPerson(false)
   }

   logInfo(tag, "single    -> SeedDatabase")
   single<SeedDatabase> {
      SeedDatabase(
         _database = get<AppDatabase>(),
         _personDao = get<IPersonDao>(),
         _seed = get<Seed>(),
         _coroutineDispatcher = get<TestDispatcher>(),
      )
   }

   logInfo(tag, "single    -> AppDatabase")
   single<AppDatabase> {
//      Room.inMemoryDatabaseBuilder(
//         androidContext(),
//         AppDatabase::class.java
//      ).build()
    Room.databaseBuilder(
       context = androidContext(),
       klass = AppDatabase::class.java,
       name = AppStart.DATABASE_NAME+"_Test"
    ).build()
   }

   logInfo(tag, "single    -> IPersonDao")
   single<IPersonDao> { get<AppDatabase>().createPersonDao() }

   logInfo(tag, "single    -> IAddressDao")
   single<IAddressDao> { get<AppDatabase>().createAddressDao() }

   logInfo(tag, "single    -> ICarDao")
   single<ICarDao> { get<AppDatabase>().createCarDao() }

   logInfo(tag, "single    -> IMovieDao")
   single<IMovieDao> { get<AppDatabase>().createMovieDao() }

   logInfo(tag, "single    -> IPersonMovieDao")
   single<IPersonMovieDao> { get<AppDatabase>().createPersonMovieDao() }

   logInfo(tag, "single    -> ITicketDao")
   single<ITicketDao> { get<AppDatabase>().createTicketDao() }

   // Provide IPersonRepository
   logInfo(tag, "single    -> PersonRepository: IPersonRepository")
   single<IPersonRepository> {
      PersonRepository(
         _personDao = get<IPersonDao>(),
         _dispatcher = get<TestDispatcher>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
   logInfo(tag, "single    -> AddressRepository: IAddressRepository")
   single<IAddressRepository> {
      AddressRepository(
         _addressDao = get<IAddressDao>(),
         _dispatcher = get<TestDispatcher>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
   logInfo(tag, "single    -> CarRepository: ICarRepository")
   single<ICarRepository> {
      CarRepository(
         _carDao = get<ICarDao>(),
         _dispatcher = get<TestDispatcher>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
   logInfo(tag, "single    -> MovieRepository: IMovieRepository")
   single<IMovieRepository> {
      MovieRepository(
         _movieDao = get<IMovieDao>(),
         _dispatcher = get<TestDispatcher>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
}