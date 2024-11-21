package de.rogallab.mobile.data.local.repositories

import de.rogallab.mobile.data.local.IMovieDao
import de.rogallab.mobile.data.local.dtos.MovieDto
import de.rogallab.mobile.domain.IMovieRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Movie
import de.rogallab.mobile.domain.mapping.toMovie
import de.rogallab.mobile.domain.mapping.toMovieDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class MovieRepository(
   private val _movieDao: IMovieDao,
   private val _coroutineDispatcher: CoroutineDispatcher
): BaseRepository<Movie, MovieDto, IMovieDao>(
   _dao = _movieDao,
   _coroutineDispatcher = _coroutineDispatcher,
   transformToDto = { it.toMovieDto() }
), IMovieRepository {

   override fun getAll(): Flow<ResultData<List<Movie>>> = flow {
      try {
         _movieDao.selectAll().collect { movieDtos: List<MovieDto> ->
            val movies: List<Movie> = movieDtos.map { it.toMovie() }
            emit(ResultData.Success( movies ))
         }
      } catch (t: Throwable) {
         emit(ResultData.Error(t))
      }
   }.flowOn(_coroutineDispatcher)

   override suspend fun getById(id: String): ResultData<Movie?> =
      withContext(_coroutineDispatcher) {
         return@withContext try {
            val movieDto: MovieDto? = _movieDao.selectById(id)
            val movie: Movie? = movieDto?.toMovie()
            ResultData.Success( movie )
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   override suspend fun count(): ResultData<Int> =
      withContext(_coroutineDispatcher) {
         return@withContext try {
            ResultData.Success(_movieDao.count())
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   companion object {
      private const val TAG = "<-MovieRepository"
   }
}