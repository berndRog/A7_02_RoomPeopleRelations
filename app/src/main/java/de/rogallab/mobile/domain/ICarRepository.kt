package de.rogallab.mobile.domain
import de.rogallab.mobile.domain.entities.Car
import kotlinx.coroutines.flow.Flow

interface ICarRepository: IBaseRepository<Car> {
    fun getAll(): Flow<ResultData<List<Car>>>
    suspend fun getById(id: String): ResultData<Car?>
    suspend fun count(): ResultData<Int>
}