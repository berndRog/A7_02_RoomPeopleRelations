package de.rogallab.mobile

import androidx.room.Room
import de.rogallab.mobile.data.local.IAddressDao
import de.rogallab.mobile.data.local.ICarDao
import de.rogallab.mobile.data.local.IMovieDao
import de.rogallab.mobile.data.local.IPersonDao
import de.rogallab.mobile.data.local.IPersonMovieDao
import de.rogallab.mobile.data.local.ITicketDao
import de.rogallab.mobile.data.Seed
import de.rogallab.mobile.data.local.database.AppDatabase
import de.rogallab.mobile.data.local.database.SeedDatabase
import de.rogallab.mobile.data.repositories.AddressRepository
import de.rogallab.mobile.data.repositories.CarRepository
import de.rogallab.mobile.data.repositories.PeopleRepository
import de.rogallab.mobile.domain.IAddressRepository
import de.rogallab.mobile.domain.ICarRepository
import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.features.cars.CarsViewModel
import de.rogallab.mobile.ui.features.people.PeopleViewModel
import de.rogallab.mobile.ui.features.people.PersonValidator
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val uiModules: Module = module {
   val tag = "<-uiModules"

   logInfo(tag, "single    -> PersonValidator")
   single<PersonValidator> { PersonValidator(androidContext()) }

   logInfo(tag, "viewModel -> PeopleViewModel")
   viewModel<PeopleViewModel> {
      PeopleViewModel(
         get<IPeopleRepository>(),
         get<PersonValidator>()
      )
   }

   logInfo(tag, "viewModel -> CarsViewModel")
   viewModel<CarsViewModel> {
      CarsViewModel(
         get<IPeopleRepository>(),
         get<ICarRepository>()
      )
   }
}

private val tag = "<-domainModules"

typealias CoroutineContextMain = CoroutineContext
typealias CoroutineContextIO = CoroutineContext


private val _coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
   // Handle the exception here
   logError(tag, "Coroutine exception: ${exception.localizedMessage}")
}

val domainModules: Module = module {

}

val dataModules = module {
   val tag = "<-dataModules"

   logInfo(tag, "single    -> Seed")
   single<Seed> {
      Seed(
         androidContext(),
         androidContext().resources
      )
   }

   logInfo(tag, "single    -> SeedDatabase")
   single<SeedDatabase> {
      SeedDatabase(
         get<IPersonDao>(),
         get<IAddressDao>(),
         get<ICarDao>(),
         get<IMovieDao>(),
         get<IPersonMovieDao>(),
         get<ITicketDao>(),
         Dispatchers.IO,
         get<Seed>()
      )
   }

   logInfo(tag, "single    -> AppDatabase")
   single {
      Room.databaseBuilder(
         androidContext(),
         AppDatabase::class.java,
         AppStart.DATABASENAME
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

   // Provide IPeopleRepository
   logInfo(tag, "single    -> PeopleRepository: IPeopleRepository")
   single<IPeopleRepository> {
      PeopleRepository(
         get<IPersonDao>(),
         Dispatchers.IO
      )
   }
   logInfo(tag, "single    -> AddressRepository: IAddressRepository")
   single<IAddressRepository> {
      AddressRepository(
         get<IAddressDao>(),
         Dispatchers.IO
      )
   }
   logInfo(tag, "single    -> CarRepository: ICarRepository")
   single<ICarRepository> {
      CarRepository(
         get<ICarDao>(),
         Dispatchers.IO
      )
   }

}
