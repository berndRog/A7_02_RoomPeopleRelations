package de.rogallab.mobile.ui.features.cars

import de.rogallab.mobile.domain.entities.Car

sealed class CarIntent {
   data class  MakerChange(val firstName: String) : CarIntent()
   data class  ModelChange(val lastName: String) : CarIntent()

   data object Clear : CarIntent()
   data class  FetchById(val id: String) : CarIntent()
   data object Create : CarIntent()
   data object Update : CarIntent()
   data class  Remove(val car: Car) : CarIntent()
}