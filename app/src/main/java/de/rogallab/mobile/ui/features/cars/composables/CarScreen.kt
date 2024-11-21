package de.rogallab.mobile.ui.features.cars.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.ui.errors.ErrorParams
import de.rogallab.mobile.ui.errors.ErrorState
import de.rogallab.mobile.ui.errors.showError
import de.rogallab.mobile.ui.features.cars.CarIntent
import de.rogallab.mobile.ui.features.cars.CarUiState
import de.rogallab.mobile.ui.features.cars.CarValidator
import de.rogallab.mobile.ui.features.cars.CarsViewModel
import de.rogallab.mobile.ui.features.people.composables.InputName
import de.rogallab.mobile.ui.features.people.composables.SelectAndShowImage
import de.rogallab.mobile.ui.navigation.NavEvent
import de.rogallab.mobile.ui.navigation.NavScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarScreen(
   viewModel: CarsViewModel = koinViewModel(),
   validator: CarValidator = koinInject(),
   isInputScreen: Boolean = true,
   id: String? = null
) {
   // is screen used as InputScreen to create a new person
   // or as DetailScreen to update a person
   val isInputMode: Boolean by rememberSaveable { mutableStateOf(isInputScreen) }

   val screenTitle = if (isInputMode) stringResource(R.string.personInput)
   else stringResource(R.string.personDetail)
   val tag = if (isInputMode) "<-CarInputScreen"
   else "<-CarDetailScreen"

   // DetailScreen
   if (!isInputMode) {
      id?.let { it: String ->
         LaunchedEffect(Unit) {
            viewModel.onProcessCarIntent(CarIntent.FetchById(it))
         }
      } ?: run {
         viewModel.onErrorEvent(
            ErrorParams(
               message = "No id for car is given",
               navEvent = NavEvent.NavigateBack(NavScreen.CarsList.route)
            )
         )
      }
   }

   // Observe the PersonUiState
   val carUiState: CarUiState
      by viewModel.carUiStateFlow.collectAsStateWithLifecycle()

   BackHandler{
      viewModel.onNavigate(NavEvent.NavigateBack(NavScreen.CarsList.route))
   }

   val windowInsets = WindowInsets.systemBars
      .add(WindowInsets.captionBar)
      .add(WindowInsets.ime)
      .add(WindowInsets.safeGestures)

   val snackbarHostState = remember { SnackbarHostState() }

   Scaffold(
      modifier = Modifier
         .fillMaxSize()
         .padding(windowInsets.asPaddingValues())
         .background(color = MaterialTheme.colorScheme.surface),
      topBar = {
         TopAppBar(
            title = { Text(text = screenTitle) },
            navigationIcon = {
               IconButton(onClick = {
                  if(viewModel.validate(isInputMode) )
                     viewModel.onNavigate(NavEvent.NavigateReverse(NavScreen.CarsList.route))
               }) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                     contentDescription = stringResource(R.string.back)) }
            }
         )
      },
      snackbarHost = {
         SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(
               snackbarData = data,
               actionOnNewLine = true
            )
         }
      }) { paddingValues: PaddingValues ->
      Column(
         modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .imePadding() // padding for the bottom for the IME
      ) {
         InputName(
            name = carUiState.car.maker,              // State ↓
            onNameChange = {                          // Event ↑
               viewModel.onProcessCarIntent(CarIntent.MakerChange(it)) },
            label = stringResource(R.string.firstName),        // State ↓
            validateName = validator::validateMaker   // Event ↑ no state change
         )
         InputName(
            name = carUiState.car.model,              // State ↓
            onNameChange = {                          // Event ↑
               viewModel.onProcessCarIntent(CarIntent.ModelChange(it)) },
            label = stringResource(R.string.lastName),       // State ↓
            validateName = validator::validateModel,  // Event ↑ no state change
         )

         SelectAndShowImage(
            imageUrl = null, // carUiState.car.imagePath,     // State ↓viewModel.imagePath,                          // State ↓
            onImageUrlChange = { Unit }                    // Event ↑
         )

      } // Column
   } // Scaffold

   val errorState: ErrorState
      by viewModel.errorStateFlow.collectAsStateWithLifecycle()
   LaunchedEffect(errorState.params) {
      errorState.params?.let { params: ErrorParams ->
         logDebug(tag, "ErrorState: ${errorState.params}")
         // show the error with a snackbar
         showError(snackbarHostState, params, viewModel::onNavigate )
         // reset the errorState, params are copied to showError
         viewModel::onErrorEventHandled
      }
   }
}