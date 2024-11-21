package de.rogallab.mobile.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import de.rogallab.mobile.ui.base.BaseActivity
import de.rogallab.mobile.ui.navigation.composables.AppNavHost
import de.rogallab.mobile.ui.theme.AppTheme

class MainActivity : BaseActivity(TAG) {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContent {
         AppTheme {
            AppNavHost()
         }
      }
   }

   companion object {
      private const val TAG = "<-MainActivity"
   }
}
