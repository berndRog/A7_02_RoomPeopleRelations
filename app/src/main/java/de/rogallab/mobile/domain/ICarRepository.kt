package de.rogallab.mobile.domain
import de.rogallab.mobile.domain.entities.Car
import kotlinx.coroutines.flow.Flow

interface ICarRepository: IBaseRepository<Car> {
    fun selectAll(): Flow<ResultData<List<Car>>>
    suspend fun findById(id: String): ResultData<Car?>
}