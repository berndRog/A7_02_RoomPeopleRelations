package de.rogallab.mobile.ui.features.people

import androidx.compose.runtime.Immutable
import de.rogallab.mobile.domain.entities.Person

@Immutable
data class PersonUiState(
   val person: Person = Person(),
   val throwable: Throwable? = null
)