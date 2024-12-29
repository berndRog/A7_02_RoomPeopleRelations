package de.rogallab.mobile.ui.features.cars.composables

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logVerbose
import de.rogallab.mobile.ui.errors.ErrorParams
import de.rogallab.mobile.ui.errors.ErrorState
import de.rogallab.mobile.ui.errors.showError
import de.rogallab.mobile.ui.features.cars.CarIntent
import de.rogallab.mobile.ui.features.cars.CarsIntent
import de.rogallab.mobile.ui.features.cars.CarViewModel
import de.rogallab.mobile.ui.navigation.NavEvent
import de.rogallab.mobile.ui.navigation.NavScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarsListScreen(
   viewModel: CarViewModel = viewModel(),
) {
   val tag = "<-CarsListScreen"

   // Observe the peopleUiState of the viewmodel
   val carsUiState by viewModel.carsUiStateFlow.collectAsStateWithLifecycle()

   // read all people from repository, when the screen is created
   LaunchedEffect(Unit) {
      logVerbose(tag, "fetchCars()")
      viewModel.onProcessCarsIntent(CarsIntent.Fetch)
   }
   
   // Back navigation
   val activity = LocalContext.current as Activity
   BackHandler(
      enabled = true,
      onBack = {  activity.finish() }
   )

   val snackbarHostState = remember { SnackbarHostState() }

   val windowInsets = WindowInsets.systemBars
      .add(WindowInsets.safeGestures)

   Scaffold(
      modifier = Modifier
         .fillMaxSize()
         .padding(windowInsets.asPaddingValues())
         .background(color = MaterialTheme.colorScheme.surface),
      topBar = {
         TopAppBar(
            title = { Text(text = stringResource(R.string.peopleList)) },
            navigationIcon = {
               IconButton(
                  onClick = {
                     logDebug(tag, "Lateral Navigation: finish app")
                     // Finish the app
                     activity.finish()
                  }
               ) { Icon(imageVector = Icons.Default.Menu,
                     contentDescription = stringResource(R.string.back)) }
            }
         )
      },
      floatingActionButton = {
         FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiary,
            onClick = {
               // FAB clicked -> InputScreen initialized
               viewModel.onProcessCarIntent(CarIntent.Clear)
               viewModel.onNavigate(NavEvent.NavigateForward(NavScreen.CarInput.route))
            }
         ) { Icon(Icons.Default.Add, "Add a contact") }
      },
      snackbarHost = {
         SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(snackbarData = data, actionOnNewLine = true)
         }
      }
   ) { paddingValues: PaddingValues ->

      LazyColumn(
         modifier = Modifier
            .padding(paddingValues = paddingValues)
            .padding(horizontal = 16.dp)
      ) {
         items(
            items = carsUiState.cars.sortedBy { it.maker },
            key = { it: Car -> it.id }
         ) { car ->

            SwipeCarListItem(
               car = car,                        // item
               onNavigate = viewModel::onNavigate,     // navigate to DetailScreen
               onProcessIntent = {                     // remove item
                  viewModel.onProcessCarIntent(CarIntent.Remove(car)) },
               onErrorEvent = viewModel::onErrorEvent, // undo -> show snackbar
               onUndoAction = viewModel::undoRemove    // undo -> action
            ) {
               // content
               CarCard(
                  maker = car.maker,
                  model = car.model,
                  imagePath = null //car.imagePath,
               )
            }
        }
      }
   }

   val errorState: ErrorState
      by viewModel.errorStateFlow.collectAsStateWithLifecycle()

   LaunchedEffect(errorState.params) {
      errorState.params?.let { params: ErrorParams ->
         logDebug(tag, "ErrorUiState: ${errorState.params}")
         // show the error with a snackbar
         showError(snackbarHostState, params, viewModel::onNavigate )
         // reset the errorState, params are copied to showError
         viewModel.onErrorEventHandled()
      }
   }
}