package de.rogallab.mobile.domain.entities

import de.rogallab.mobile.domain.utilities.newUuid

data class Movie(
   val title: String = "",
   val director: String = "",
   val id: String = newUuid(),
)