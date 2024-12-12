package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.dtos.MovieDto
import de.rogallab.mobile.data.local.IMovieDao
import de.rogallab.mobile.data.mapping.toMovie
import de.rogallab.mobile.data.mapping.toMovieDto
import de.rogallab.mobile.domain.IMovieRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Movie
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class MovieRepository(
   private val _movieDao: IMovieDao,
   private val _dispatcher: CoroutineDispatcher,
   private val _exceptionHandler: CoroutineExceptionHandler
): BaseRepository<Movie, MovieDto, IMovieDao>(
   _dao = _movieDao,
   _dispatcher = _dispatcher,
   _exceptionHandler = _exceptionHandler,
   transformToDto = { it.toMovieDto() }
), IMovieRepository {

   override fun selectAll(): Flow<ResultData<List<Movie>>> = flow {
      try {
         _movieDao.selectAll().collect { movieDtos: List<MovieDto> ->
            val movies: List<Movie> = movieDtos.map { it.toMovie() }
            emit(ResultData.Success( movies ))
         }
      } catch (t: Throwable) {
         emit(ResultData.Error(t))
      }
   }.flowOn(_dispatcher+_exceptionHandler)

   override suspend fun findById(id: String): ResultData<Movie?> =
      withContext(_dispatcher+_exceptionHandler) {
         return@withContext try {
            val movie = _movieDao.findById(id)?.toMovie()
            ResultData.Success( movie )
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }

   companion object {
      private const val TAG = "<-MovieRepository"
   }
}