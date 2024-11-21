package de.rogallab.mobile.ui.features.cars

import androidx.compose.runtime.Immutable
import de.rogallab.mobile.domain.entities.Car

@Immutable
data class CarsUiState(
   val isLoading: Boolean = false,
   val isSuccessful: Boolean = false,
   val cars: List<Car> = emptyList()
)