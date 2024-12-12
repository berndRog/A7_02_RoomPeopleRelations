package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.dtos.CarDto
import de.rogallab.mobile.data.local.ICarDao
import de.rogallab.mobile.data.mapping.toCar
import de.rogallab.mobile.data.mapping.toCarDto
import de.rogallab.mobile.domain.ICarRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Car
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CarRepository(
   private val _carDao: ICarDao,
   private val _dispatcher: CoroutineDispatcher,
   private val _exceptionHandler: CoroutineExceptionHandler
): BaseRepository<Car, CarDto, ICarDao>(
   _dao = _carDao,
   _dispatcher = _dispatcher,
   _exceptionHandler = _exceptionHandler,
   transformToDto = { it.toCarDto() }
), ICarRepository {

   override fun selectAll(): Flow<ResultData<List<Car>>> = flow {
      try {
         _carDao.selectAll().collect { carDtos: List<CarDto> ->
            val cars: List<Car> = carDtos.map { it.toCar() }
            emit(ResultData.Success( cars ))
         }
      } catch (t: Throwable) {
         emit(ResultData.Error(t))
      }
   }.flowOn(_dispatcher+_exceptionHandler)

   override suspend fun findById(id: String): ResultData<Car?> =
      withContext(_dispatcher+_exceptionHandler) {
         return@withContext try {
            val car = _carDao.findById(id)?.toCar()
            ResultData.Success( car )
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   companion object {
      private const val TAG = "<-CarRepository"
   }
}