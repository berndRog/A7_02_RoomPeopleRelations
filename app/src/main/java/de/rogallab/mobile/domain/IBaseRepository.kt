package de.rogallab.mobile.domain

interface IBaseRepository<T> {
   suspend fun insert(item: T): ResultData<Unit>
   suspend fun insert(items: List<T>): ResultData<Unit>
   suspend fun update(item: T): ResultData<Unit>
   suspend fun remove(item: T): ResultData<Unit>
}