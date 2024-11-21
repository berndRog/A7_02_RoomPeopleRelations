package de.rogallab.mobile.ui.features.cars

import android.content.Context
import de.rogallab.mobile.R

class CarValidator(
   private val _context: Context
) {

   private val _charMin: Int by lazy {
      _context.getString(R.string.errorCharMin).toInt()
   }

   private val _charMax: Int by lazy {
      _context.getString(R.string.errorCharMax).toInt()
   }

   private val _makerTooShort: String by lazy {
      _context.getString(R.string.errorFirstNameTooShort)
   }
   private val _makerTooLong: String by lazy {
      _context.getString(R.string.errorFirstNameTooLong)
   }
   private val _modelTooShort: String by lazy {
      _context.getString(R.string.errorLastNameTooShort)
   }
   private val _modelTooLong: String by lazy {
      _context.getString(R.string.errorLastNameTooLong)
   }

   // Validation is unrelated to state management and simply returns a result
   // We can call the validation function directly in the Composables
   fun validateMaker(maker: String): Pair<Boolean, String> =
      if (maker.isEmpty() || maker.length < _charMin)
         Pair(true, _makerTooShort)
      else if (maker.length > _charMax )
         Pair(true, _makerTooLong)
      else
         Pair(false, "")

   fun validateModel(model: String): Pair<Boolean, String> =
      if (model.isEmpty() || model.length < _charMin)
         Pair(true, _modelTooShort)
      else if (model.length > _charMax )
         Pair(true, _modelTooLong)
      else
         Pair(false, "")


}



