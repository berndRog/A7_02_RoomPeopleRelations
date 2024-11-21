package de.rogallab.mobile.ui.features.cars.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.entities.Car
import de.rogallab.mobile.ui.errors.ErrorParams
import de.rogallab.mobile.ui.features.cars.CarIntent
import de.rogallab.mobile.ui.features.people.composables.SetSwipeBackground
import de.rogallab.mobile.ui.navigation.NavEvent
import de.rogallab.mobile.ui.navigation.NavScreen
import kotlinx.coroutines.delay

@Composable
fun SwipeCarListItem(
   car: Car,
   onNavigate: (NavEvent) -> Unit,
   onProcessIntent: (CarIntent) -> Unit,
   onErrorEvent: (ErrorParams) -> Unit,
   onUndoAction: () -> Unit,
   animationDuration: Int = 1000,
   content: @androidx.compose.runtime.Composable () -> Unit
) {

   var isRemoved by remember{ mutableStateOf(false) }
   var isUndo by remember{ mutableStateOf(false) }
   var hasNavigated by remember { mutableStateOf(false) }

   val state: SwipeToDismissBoxState =
      rememberSwipeToDismissBoxState(
         initialValue = SwipeToDismissBoxValue.Settled,
         confirmValueChange = { value: SwipeToDismissBoxValue ->
            if (value == SwipeToDismissBoxValue.StartToEnd && !hasNavigated) {
               onNavigate(
                  NavEvent.NavigateForward(NavScreen.CarDetail.route + "/${car.id}"))
               hasNavigated = true  // call only once
               return@rememberSwipeToDismissBoxState true
            } else if (value == SwipeToDismissBoxValue.EndToStart) {
               isRemoved = true  // with animation
               return@rememberSwipeToDismissBoxState true
            } else return@rememberSwipeToDismissBoxState false
         },
         positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold,
      )

   val undoDeletePerson = stringResource(R.string.undoDeletePerson)
   val undoAnswer = stringResource(R.string.undoAnswer)

   LaunchedEffect(key1 = isRemoved) {
      if(isRemoved) {
         delay(animationDuration.toLong())
         onProcessIntent(CarIntent.Remove(car))
         // undo remove?
         var params = ErrorParams(
            message = undoDeletePerson,
            actionLabel = undoAnswer,
            duration = SnackbarDuration.Short,
            withUndoAction = true,
            onUndoAction = onUndoAction,
            navEvent = NavEvent.NavigateReverse(route = NavScreen.CarsList.route)
         )
         onErrorEvent(params)
      }
   }

   AnimatedVisibility(
      visible = !isRemoved,
      exit = shrinkVertically(
         animationSpec = tween(durationMillis = animationDuration),
         shrinkTowards = Alignment.Top
      ) + fadeOut()
   ) {

      SwipeToDismissBox(
         state = state,
         backgroundContent = { SetSwipeBackground(state) },
         modifier = Modifier.padding(vertical = 4.dp),
         // enable dismiss from start to end (left to right)
         enableDismissFromStartToEnd = true,
         // enable dismiss from end to start (right to left)
         enableDismissFromEndToStart = true
      ) {
         content()
      }
   }
}
