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
import de.rogallab.mobile.ui.features.cars.CarsViewModel
import de.rogallab.mobile.ui.features.people.PeopleViewModel
import de.rogallab.mobile.ui.features.people.PersonValidator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val uiModules: Module = module {
   val tag = "<-uiModules"

   logInfo(tag, "single    -> PersonValidator")
   single<PersonValidator> { PersonValidator(androidContext()) }

   logInfo(tag, "viewModel -> PeopleViewModel")
   viewModel<PeopleViewModel> {
      PeopleViewModel(
         _repository = get<IPersonRepository>(),
         _validator = get<PersonValidator>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
//   viewModel<PeopleViewModel> {
//      PeopleViewModel(
//         _repository = get { parametersOf(get<PeopleViewModel>().viewModelScope) },
//         _validator = get<PersonValidator>()
//      )
//   }

   logInfo(tag, "viewModel -> CarsViewModel")
   viewModel<CarsViewModel> {
      CarsViewModel(
         _peopleRepository = get<IPersonRepository>(),
         _carRepository = get<ICarRepository>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
}

val domainModules: Module = module {
   val tag = "<-domainModules"

   logInfo(tag, "single    -> CoroutineExceptionHandler")
   single<CoroutineExceptionHandler> {
      CoroutineExceptionHandler { _, exception ->
         logError(tag, "Coroutine exception: ${exception.localizedMessage}")
      }
   }
   logInfo(tag, "single    -> named(DispatcherIO)")
   single<CoroutineDispatcher>(named("DispatcherIO")) { Dispatchers.IO }
   logInfo(tag, "single    -> named(DispatcherMain)")
   single<CoroutineDispatcher>(named("DispatcherMain")) { Dispatchers.Main }
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
         _coroutineDispatcher = get<CoroutineDispatcher>(named("DispatcherIO"))
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
         _dispatcher = get<CoroutineDispatcher>(named("DispatcherIO")),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
//   factory<IPersonRepository> { (viewModelScope: CoroutineScope) ->
//      PersonRepository(
//         _viewModelScope = viewModelScope,
//         _personDao = get<IPersonDao>(),
//         _coroutineDispatcher = Dispatchers.IO
//      )
//   }
   logInfo(tag, "single    -> AddressRepository: IAddressRepository")
   single<IAddressRepository> {
      AddressRepository(
         _addressDao = get<IAddressDao>(),
         _dispatcher = get<CoroutineDispatcher>(named("DispatcherIO")),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
   logInfo(tag, "single    -> CarRepository: ICarRepository")
   single<ICarRepository> {
      CarRepository(
         _carDao = get<ICarDao>(),
         _dispatcher = get<CoroutineDispatcher>(named("DispatcherIO")),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
   logInfo(tag, "single    -> MovieRepository: IMovieRepository")
   single<IMovieRepository> {
      MovieRepository(
         _movieDao = get<IMovieDao>(),
         _dispatcher = get<CoroutineDispatcher>(named("DispatcherIO")),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
}
