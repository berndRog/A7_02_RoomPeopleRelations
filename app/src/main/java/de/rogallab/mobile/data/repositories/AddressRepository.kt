package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.dtos.AddressDto
import de.rogallab.mobile.data.local.IAddressDao
import de.rogallab.mobile.data.mapping.toAddress
import de.rogallab.mobile.data.mapping.toAddressDto
import de.rogallab.mobile.domain.IAddressRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Address
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.withContext

class AddressRepository(
   private val _addressDao: IAddressDao,
   private val _dispatcher: CoroutineDispatcher,
   private val _exceptionHandler: CoroutineExceptionHandler
) : BaseRepository<Address, AddressDto, IAddressDao>(
   _dao = _addressDao,
   _dispatcher = _dispatcher,
   _exceptionHandler = _exceptionHandler,
   transformToDto = { it.toAddressDto() }
), IAddressRepository {

   override suspend fun findById(id: String): ResultData<Address?> =
      withContext(_dispatcher+ _exceptionHandler) {
         return@withContext try {
            ResultData.Success(_addressDao.findById(id)?.toAddress())
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }
   companion object {
      private const val TAG = "<-AddressRepository"
   }
}