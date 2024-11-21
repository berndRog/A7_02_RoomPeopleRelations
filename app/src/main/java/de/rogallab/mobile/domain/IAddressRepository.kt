package de.rogallab.mobile.domain

import de.rogallab.mobile.domain.entities.Address

interface IAddressRepository: IBaseRepository<Address> {
    suspend fun getById(id: String): ResultData<Address?>
}