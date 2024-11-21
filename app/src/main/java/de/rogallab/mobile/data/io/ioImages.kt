package de.rogallab.mobile.data.local.io

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.net.toFile
import de.rogallab.mobile.domain.utilities.logError
import java.io.File
import java.io.IOException
import java.util.*

fun readImageFromStorage(
   uri: Uri
): Bitmap? =
   try {
      BitmapFactory.decodeFile(uri.toFile().absolutePath)
         ?: throw IOException("BitmapFactory.decodeFile() returned null")
   } catch (e: IOException) {
      e.localizedMessage?.let { logError("<-readImageFromInternalStorage", it) }
      throw e
   }

fun writeImageToStorage(
   context: Context,
   bitmap: Bitmap
): String? =
   try {
      val file = File(context.filesDir, "${UUID.randomUUID()}.jpg")
      // compress bitmap to file and return absolute path
      file.outputStream().use { out ->
         bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
         file.absolutePath // return absolute path
      }
   } catch (e: IOException) {
      e.localizedMessage?.let { logError("<-writeImageToInternalStorage", it) }
      logError("<-writeImageToInternalStorage", e.message!!)
      throw e
   }

fun deleteFileOnStorage(fileName:String) {
   try {
      File(fileName).apply {
         this.absoluteFile.delete()
      }
   } catch(e:IOException ) {
      e.localizedMessage?.let { logError("<-deleteFileOnInternalStorage", it) }
      throw e
   }
}