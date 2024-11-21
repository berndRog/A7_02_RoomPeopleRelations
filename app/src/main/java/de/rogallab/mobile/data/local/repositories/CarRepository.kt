package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.local.ICarDao
import de.rogallab.mobile.data.local.dtos.CarDto
import de.rogallab.mobile.data.local.repositories.BaseRepository
import de.rogallab.mobile.domain.ICarRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.domain.mapping.toCar
import de.rogallab.mobile.domain.mapping.toCarDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CarRepository(
   private val _carDao: ICarDao,
   private val _coroutineDispatcher: CoroutineDispatcher
): BaseRepository<Car, CarDto, ICarDao>(
   _dao = _carDao,
   _coroutineDispatcher = _coroutineDispatcher,
   transformToDto = { it.toCarDto() }
), ICarRepository {

   override fun getAll(): Flow<ResultData<List<Car>>> = flow {
      try {
         _carDao.selectAll().collect { carDtos: List<CarDto> ->
            val cars: List<Car> = carDtos.map { it.toCar() }
            emit(ResultData.Success( cars ))
         }
      } catch (t: Throwable) {
         emit(ResultData.Error(t))
      }
   }.flowOn(_coroutineDispatcher)

   override suspend fun getById(id: String): ResultData<Car?> =
      withContext(_coroutineDispatcher) {
         return@withContext try {
            val carDto: CarDto? = _carDao.selectById(id)
            val car: Car? = carDto?.toCar()
            ResultData.Success( car )
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   override suspend fun count(): ResultData<Int> =
      withContext(_coroutineDispatcher) {
         return@withContext try {
            ResultData.Success(_carDao.count())
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   companion object {
      private const val TAG = "<-PeopleRepository"
   }
}