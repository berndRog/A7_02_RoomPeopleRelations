package de.rogallab.mobile.domain.entities

import de.rogallab.mobile.domain.utilities.emptyUuid
import de.rogallab.mobile.domain.utilities.newUuid

data class Address(
   val city: String = "",
   val postCode: String = "",
   val id: String = newUuid(),
   // Address -> [0..1] Person (is it really need?)
   // leads to a circular dependency which is not recommended
   // val person: Person? = null,
   // fk: personId with this address
   val personId: String? = emptyUuid(),
)
