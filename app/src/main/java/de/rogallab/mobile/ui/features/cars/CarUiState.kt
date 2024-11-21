package de.rogallab.mobile.ui.features.cars

import androidx.compose.runtime.Immutable
import de.rogallab.mobile.domain.entities.Car

@Immutable
data class CarUiState(
   val car: Car = Car()
)