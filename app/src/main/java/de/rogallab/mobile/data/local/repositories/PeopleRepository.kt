package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.local.IPersonDao
import de.rogallab.mobile.data.local.dtos.PersonDto
import de.rogallab.mobile.data.local.repositories.BaseRepository
import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.mapping.toPerson
import de.rogallab.mobile.domain.mapping.toPersonDto
import de.rogallab.mobile.domain.utilities.logDebug
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

import kotlinx.coroutines.withContext

class PeopleRepository(
   private val _personDao: IPersonDao,
   private val _coroutineDispatcher: CoroutineDispatcher
): BaseRepository<Person, PersonDto, IPersonDao>(
   _dao = _personDao,
   _coroutineDispatcher = _coroutineDispatcher,
   transformToDto = { it.toPersonDto() }
), IPeopleRepository {

   override fun getAll(): Flow<ResultData<List<Person>>> = flow {
      try {
         _personDao.selectAll().collect { personDtos: List<PersonDto> ->
            logDebug(TAG, "getAll: ${personDtos.size}")
            val people: List<Person> = personDtos.map { it.toPerson() }
            emit(ResultData.Success(people))
         }
      } catch (t: Throwable) {
         emit(ResultData.Error(t))
      }
   }.flowOn(_coroutineDispatcher)

   override suspend fun getById(id: String): ResultData<Person?> =
      withContext(_coroutineDispatcher) {
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
      withContext(_coroutineDispatcher) {
         return@withContext try {
            ResultData.Success(_personDao.count())
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }
   /*
   override suspend fun create(person: Person): ResultData<Unit> =
      withContext(_coroutineDispatcher) {
         return@withContext try {
            logDebug(TAG, "create: ${person.id.as8()}")
            _personDao.insert(person)
            ResultData.Success(Unit)
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   override suspend fun update(person: Person): ResultData<Unit> =
      withContext(_coroutineDispatcher) {
         return@withContext try {
            logDebug(TAG, "update: ${person.id.as8()}")
            _personDao.update(person)
            ResultData.Success(Unit)
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   override suspend fun remove(person: Person): ResultData<Unit> =
      withContext(_coroutineDispatcher) {
         return@withContext try {
            logDebug(TAG, "remove: ${person.id.as8()}")
            _personDao.delete(person)
            ResultData.Success(Unit)
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }
*/
   companion object {
      private const val TAG = "<-PeopleRepository"
   }
}