package de.rogallab.mobile.ui.people

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.rogallab.mobile.domain.IPersonRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.ui.features.people.PeopleViewModel
import de.rogallab.mobile.ui.features.people.PersonIntent
import de.rogallab.mobile.ui.features.people.PersonValidator
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.Mockito.*
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PeopleViewModelTest : KoinTest {

   @get:Rule
   val instantTaskExecutorRule = InstantTaskExecutorRule()
   private val testDispatcher = StandardTestDispatcher()

   private val personRepository: IPersonRepository = mock(IPersonRepository::class.java)
   private val personValidator: PersonValidator by inject()
   private val peopleViewModel: PeopleViewModel by inject()

   @Before
   fun setUp() {
      Dispatchers.setMain(testDispatcher)
      startKoin {
         // Reference Android context
         androidLogger(Level.DEBUG)
         androidContext(ApplicationProvider.getApplicationContext())
         modules(module {
            single<PersonValidator> { PersonValidator(androidContext()) }
            single<IPersonRepository> { personRepository }
            single {
               PeopleViewModel(
                  _repository = get<IPersonRepository>(),
                  _validator = get<PersonValidator>(),
                  _exceptionHandler = get<CoroutineExceptionHandler>()
               )
            }
         })
      }
   }

   @After
   fun tearDown() {
      stopKoin()
      Dispatchers.resetMain()
   }

//   @Test
//   fun testGetPeople() = runTest {
//      // Arrange
//      val personList = listOf(Person("1", "John", "Doe"))
//      `when`(personRepository.getAll()).thenReturn(flowOf(ResultData.Success(personList)))
//
//      // Act
//      peopleViewModel.getPeople()
//
//      // Assert
//      assertEquals(personList, peopleViewModel.people.value)
//   }


   @Test
   fun testOnProcessPersonIntentFirstNameChange() = runTest {
      // Arrange
      val firstName = "Arne"
      // Act
      peopleViewModel.onProcessPersonIntent(PersonIntent.Clear)
      peopleViewModel.onProcessPersonIntent(PersonIntent.FirstNameChange(firstName))
      // Assert
      val actual = peopleViewModel.personUiStateFlow.value.person.firstName
      assertEquals(firstName, actual)

   }
   @Test
   fun testCreatePerson() = runTest {
      // Arrange
      val newPerson = Person("1", "John", "Doe")
      `when`(personRepository.insert(newPerson)).thenReturn(ResultData.Success(Unit))

      // Act
      peopleViewModel.onProcessPersonIntent(PersonIntent.Clear)
      peopleViewModel.onProcessPersonIntent(PersonIntent.FirstNameChange(newPerson.firstName))
      peopleViewModel.onProcessPersonIntent(PersonIntent.LastNameChange(newPerson.lastName))
      peopleViewModel.onProcessPersonIntent(PersonIntent.Create)

      // Assert
      verify(personRepository).insert(newPerson)
      val actual = peopleViewModel.personUiStateFlow.value.person
      assertEquals(newPerson, actual)
   }
}