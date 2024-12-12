package de.rogallab.mobile.domain
import de.rogallab.mobile.domain.entities.Movie
import kotlinx.coroutines.flow.Flow

interface IMovieRepository: IBaseRepository<Movie> {
    fun selectAll(): Flow<ResultData<List<Movie>>>
    suspend fun findById(id: String): ResultData<Movie?>
}