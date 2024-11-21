package de.rogallab.mobile.domain
import de.rogallab.mobile.domain.entities.Movie
import kotlinx.coroutines.flow.Flow

interface IMovieRepository: IBaseRepository<Movie> {
    fun getAll(): Flow<ResultData<List<Movie>>>
    suspend fun getById(id: String): ResultData<Movie?>
    suspend fun count(): ResultData<Int>
}