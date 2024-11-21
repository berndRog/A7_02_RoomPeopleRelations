package de.rogallab.mobile.ui.features.cars

sealed class CarsIntent {
   data object Fetch : CarsIntent()
}