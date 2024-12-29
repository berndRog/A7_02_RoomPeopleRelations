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
import de.rogallab.mobile.ui.IErrorHandler
import de.rogallab.mobile.ui.INavigationHandler
import de.rogallab.mobile.ui.errors.ErrorHandler
import de.rogallab.mobile.ui.features.cars.CarViewModel
import de.rogallab.mobile.ui.features.people.PersonViewModel
import de.rogallab.mobile.ui.features.people.PersonValidator
import de.rogallab.mobile.ui.navigation.NavigationHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

typealias CoroutineDispatcherMain = CoroutineDispatcher
typealias CoroutineDispatcherIo = CoroutineDispatcher
typealias CoroutineScopeMain = CoroutineScope
typealias CoroutineScopeIo = CoroutineScope

val domainModules: Module = module {
   val tag = "<-domainModules"


   logInfo(tag, "factory   -> CoroutineExceptionHandler")
   factory<CoroutineExceptionHandler> {
      CoroutineExceptionHandler { _, exception ->
         logError(tag, "Coroutine exception: ${exception.localizedMessage}")
      }
   }

   logInfo( tag, "factory  -> CoroutineDispatcherMain")
   factory<CoroutineDispatcherMain> { Dispatchers.Main }

   logInfo(tag, "factory   -> CoroutineDispatcherIo)")
   factory<CoroutineDispatcherIo>{ Dispatchers.IO }


   logInfo(tag, "factory   -> CoroutineScopeMain")
   factory<CoroutineScopeMain> {
      CoroutineScope(
         SupervisorJob() +
            get<CoroutineDispatcherIo>()
      )
   }

   logInfo(tag, "factory   -> CoroutineScopeIo")
   factory<CoroutineScopeIo> {
      CoroutineScope(
         SupervisorJob() +
            get<CoroutineDispatcherIo>()
      )
   }
}

val dataModules = module {
   val tag = "<-dataModules"

   logInfo(tag, "single    -> Seed")
   single<Seed> {
      Seed(
         context = androidContext(),
         resources = androidContext().resources
      ).createPerson(true)
   }

   logInfo(tag, "single    -> SeedDatabase")
   single<SeedDatabase> {
      SeedDatabase(
         _database = get<AppDatabase>(),
         _personDao = get<IPersonDao>(),
         _seed = get<Seed>(),
         _coroutineDispatcher = get<CoroutineDispatcherIo>()
      )
   }

   logInfo(tag, "single    -> AppDatabase")
   single {
      Room.databaseBuilder(
         context = androidContext(),
         klass = AppDatabase::class.java,
         name = AppStart.DATABASE_NAME
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

   // Provide IPersonRepository, injecting the `viewModelScope`
   logInfo(tag, "single    -> PersonRepository: IPersonRepository")
   single<IPersonRepository> {
      PersonRepository(
         _personDao = get<IPersonDao>(),
         _dispatcher = get<CoroutineDispatcherIo>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }

   logInfo(tag, "single    -> AddressRepository: IAddressRepository")
   single<IAddressRepository> {
      AddressRepository(
         _addressDao = get<IAddressDao>(),
         _dispatcher = get<CoroutineDispatcherIo>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
   logInfo(tag, "single    -> CarRepository: ICarRepository")
   single<ICarRepository> {
      CarRepository(
         _carDao = get<ICarDao>(),
         _dispatcher = get<CoroutineDispatcherIo>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
   logInfo(tag, "single    -> MovieRepository: IMovieRepository")
   single<IMovieRepository> {
      MovieRepository(
         _movieDao = get<IMovieDao>(),
         _dispatcher = get<CoroutineDispatcherIo>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
}

val uiModules: Module = module {
   val tag = "<-uiModules"

   logInfo(tag, "factory   -> NavigationHandler: INavigationHandler")
   factory<INavigationHandler> {
      NavigationHandler(
         _coroutineScopeMain = get<CoroutineScopeMain>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }

   logInfo(tag, "factory   -> ErrorHandler: IErrorHandler")
   factory<IErrorHandler> {
      ErrorHandler(
         _coroutineScopeMain = get<CoroutineScopeMain>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }

   logInfo(tag, "single    -> PersonValidator")
   single<PersonValidator> { PersonValidator(androidContext()) }

   logInfo(tag, "viewModel -> PersonViewModel")
   viewModel<PersonViewModel> {
      PersonViewModel(
         _repository = get<IPersonRepository>(),
         _validator = get<PersonValidator>(),
         _navigationHandler = get<INavigationHandler>(),
         _errorHandler = get<IErrorHandler>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }

   logInfo(tag, "viewModel -> CarViewModel")
   viewModel<CarViewModel> {
      CarViewModel(
         _peopleRepository = get<IPersonRepository>(),
         _carRepository = get<ICarRepository>(),
         _navigationHandler = get<INavigationHandler>(),
         _errorHandler = get<IErrorHandler>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
}
