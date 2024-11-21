package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.local.IAddressDao
import de.rogallab.mobile.data.local.dtos.AddressDto
import de.rogallab.mobile.data.local.repositories.BaseRepository
import de.rogallab.mobile.domain.IAddressRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Address
import de.rogallab.mobile.domain.mapping.toAddress
import de.rogallab.mobile.domain.mapping.toAddressDto
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AddressRepository(
   private val _addressDao: IAddressDao,
   private val _coroutineDispatcher: CoroutineDispatcher
) : BaseRepository<Address, AddressDto, IAddressDao>(
   _dao = _addressDao,
   _coroutineDispatcher = _coroutineDispatcher,
   transformToDto = { it.toAddressDto() }
), IAddressRepository {

   override suspend fun getById(id: String): ResultData<Address?> =
      withContext(_coroutineDispatcher) {
         return@withContext try {
            ResultData.Success(_addressDao.selectById(id)?.toAddress())
         } catch (t: Throwable) {
            ResultData.Error(t)
         }
      }
   companion object {
      private const val TAG = "<-AddressRepository"
   }
}