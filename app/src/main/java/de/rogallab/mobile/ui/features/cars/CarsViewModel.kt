package de.rogallab.mobile.ui.features.cars

import androidx.lifecycle.viewModelScope
import de.rogallab.mobile.domain.ICarRepository
import de.rogallab.mobile.domain.IPersonRepository
import de.rogallab.mobile.domain.ResultData
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.base.BaseViewModel
import de.rogallab.mobile.ui.errors.ErrorParams
import de.rogallab.mobile.ui.features.people.PeopleUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CarsViewModel(
   private val _peopleRepository: IPersonRepository,
   private val _carRepository: ICarRepository,
   private val _exceptionHandler: CoroutineExceptionHandler
) : BaseViewModel(_exceptionHandler, TAG) {

   private var removedCar: Car? = null

   // ===============================
   // S T A T E   C H A N G E S
   // ===============================
   // Data Binding PeopleListScreen <=> PersonViewModel
   private val _carsUiStateFlow = MutableStateFlow(CarsUiState())

   // transform intent into an action
   fun onProcessCarsIntent(intent: CarsIntent) {
      logInfo(TAG, "onProcessIntent: $intent")
      when (intent) {
         is CarsIntent.Fetch -> {} //fetch()
      }
   }

   // Basic Scenario: Use the stateIn() operator to convert a Flow from the data source into a State
   val carsUiStateFlow: StateFlow<CarsUiState> = _carRepository.selectAll()
      .map { resultData ->
         when (resultData) {
            is ResultData.Success -> _carsUiStateFlow.update {
               it.copy(cars = resultData.data.toList())
            }
            is ResultData.Error -> handleErrorEvent(resultData.throwable)
         }
         return@map _carsUiStateFlow.value
      }.stateIn(
         scope = viewModelScope,
         started = SharingStarted.WhileSubscribed(5000),
         initialValue = CarsUiState()
      )

   // Data Binding PersonScreen <=> PersonViewModel
   private val _carUiStateFlow = MutableStateFlow(CarUiState())
   val carUiStateFlow = _carUiStateFlow.asStateFlow()

   // transform intent into an action
   fun onProcessCarIntent(intent: CarIntent) {
      logInfo(TAG, "onProcessIntent: $intent")
      when (intent) {
         is CarIntent.MakerChange -> onMakerChange(intent.firstName)
         is CarIntent.ModelChange -> onModelChange(intent.lastName)

         is CarIntent.Clear -> clearState()

         is CarIntent.FetchById -> fetchById(intent.id)
         is CarIntent.Create -> create()
         is CarIntent.Update -> update()
         is CarIntent.Remove -> remove(intent.car)
      }
   }

   private fun onMakerChange(maker: String) {
      if (maker == _carUiStateFlow.value.car.maker) return
      _carUiStateFlow.update { it: CarUiState ->
         it.copy(car = it.car.copy(maker = maker))
      }
   }
   private fun onModelChange(model: String) {
      if (model == _carUiStateFlow.value.car.model) return
      _carUiStateFlow.update { it: CarUiState ->
         it.copy(car = it.car.copy(model = model))
      }
   }

   private fun fetchById(personId: String) {
      logDebug(TAG, "fetchPersonById: $personId")
      viewModelScope.launch(_exceptionHandler) {
         when (val resultData = _carRepository.findById(personId)) {
            is ResultData.Success -> _carUiStateFlow.update { it: CarUiState ->
               it.copy(car = resultData.data ?: Car())  // new UiState
            }
            is ResultData.Error -> handleErrorEvent(resultData.throwable)
         }
      }
   }

   private fun clearState() {
      _carUiStateFlow.update { it.copy(car = Car()) }
   }

   private fun create() {
      logDebug(TAG, "createCar()")
      viewModelScope.launch(_exceptionHandler) {
         when (val resultData = _carRepository.insert(_carUiStateFlow.value.car)) {
            is ResultData.Success -> {}
            is ResultData.Error -> handleErrorEvent(resultData.throwable)
         }
      }
   }
   private fun update() {
      logDebug(TAG, "updatePerson()")
      viewModelScope.launch(_exceptionHandler) {
         when (val resultData = _carRepository.update(_carUiStateFlow.value.car)) {
            is ResultData.Success -> {}
            is ResultData.Error -> handleErrorEvent(resultData.throwable)
         }
      }
   }
   private fun remove(car: Car) {
      logDebug(TAG, "removePerson()")
      viewModelScope.launch(_exceptionHandler) {
         when (val resultData = _carRepository.remove(car)) {
            is ResultData.Success -> removedCar = car
            is ResultData.Error -> handleErrorEvent(resultData.throwable)
         }
      }
   }

   fun undoRemove() {
      removedCar?.let { person ->
         logDebug(TAG, "undoRemovePerson()")
         viewModelScope.launch(_exceptionHandler) {
            when (val resultData = _carRepository.insert(person)) {
               is ResultData.Success -> removedCar = null
               is ResultData.Error -> handleErrorEvent(resultData.throwable)
            }
         }
      }
   }

   // =========================================
   // V A L I D A T E   I N P U T   F I E L D S
   // =========================================
   // validate all input fields after user finished input into the form
   fun validate(isInput: Boolean): Boolean {
      // write data to repository
      if (isInput) this.create()
      else         this.update()
      return true
   }

   companion object {
      private const val TAG = "<-CarsViewModel"
   }
}