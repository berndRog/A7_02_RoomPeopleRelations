package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.dtos.PersonDto
import de.rogallab.mobile.data.local.IPersonDao
import de.rogallab.mobile.data.mapping.toPerson
import de.rogallab.mobile.data.mapping.toPersonDto
import de.rogallab.mobile.domain.IPersonRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logDebug
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class PersonRepository(
   private val _personDao: IPersonDao,
   private val _dispatcher: CoroutineDispatcher,
   private val _exceptionHandler: CoroutineExceptionHandler
): BaseRepository<Person, PersonDto, IPersonDao>(
   _dao = _personDao,
   _dispatcher = _dispatcher,
   _exceptionHandler = _exceptionHandler,
   transformToDto = { it.toPersonDto() }
), IPersonRepository {

   override fun selectAll(): Flow<ResultData<List<Person>>> = flow {
      try {
         _personDao.selectAll().collect { personDtos: List<PersonDto> ->
            logDebug(TAG, "getAll: ${personDtos.size}")
            val people: List<Person> = personDtos.map { it.toPerson() }
            //throw RuntimeException("getAll() failed")
            emit(ResultData.Success(people))
         }
      } catch (t: Throwable) {
         emit(ResultData.Error(t))
      }
   }.flowOn(_dispatcher+_exceptionHandler)

   override suspend fun findById(id: String): ResultData<Person?> =
      withContext(_dispatcher+_exceptionHandler) {
         return@withContext try {
            logDebug(TAG, "getById()")
            val personDto: PersonDto? = _personDao.selectById(id)
            val person: Person? = personDto?.toPerson()
            ResultData.Success(person)
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   override suspend fun count(): ResultData<Int> =
      withContext(_dispatcher+_exceptionHandler) {
         return@withContext try {
            ResultData.Success(_personDao.count())
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   companion object {
      private const val TAG = "<-PersonRepository"
   }
}