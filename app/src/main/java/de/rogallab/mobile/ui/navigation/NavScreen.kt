package de.rogallab.mobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavScreen(
   val route: String,
   val title: String = "",
   val icon: ImageVector? = null
) {
   data object PeopleList: NavScreen(
      route = "PeopleListScreen",
      title = "Personen",
      icon =  Icons.Outlined.Group
   )
   data object PersonInput: NavScreen(
      route = "PersonInputScreen",
      title = "Person hinzufügen",
      icon = Icons.Outlined.PersonAdd
   )
   data object PersonDetail: NavScreen(
      route = "PersonDetailScreen",
      title = "Person ändern",
      icon = Icons.Outlined.Person
   )

   data object CarsList: NavScreen(
      route = "CarsListScreen",
      title = "Autos",
      icon =  Icons.Outlined.Group
   )
   data object CarInput: NavScreen(
      route = "CarInputScreen",
      title = "Auto hinzufügen",
      icon = Icons.Outlined.PersonAdd
   )
   data object CarDetail: NavScreen(
      route = "CarDetailScreen",
      title = "Auto ändern",
      icon = Icons.Outlined.Person
   )
}