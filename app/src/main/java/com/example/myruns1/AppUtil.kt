package com.example.myruns1

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object AppUtil {

    // Function to check for camera and storage permissions
    fun checkPermissions(activity: Activity?) {
        if (ContextCompat.checkSelfPermission(
                activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                0
            )
        }
    }

    // Function to rotate and return a bitmap from URI
    fun loadBitmapFromUri(context: Context, imgUri: Uri, rotation: Float = 0f): Bitmap {
        val inputStream = context.contentResolver.openInputStream(imgUri)
        val bitmap = BitmapFactory.decodeStream(inputStream) // Load the bitmap from URI

        // Apply rotation if necessary
        if (rotation != 0f) {
            val matrix = Matrix()
            matrix.setRotate(rotation) // Rotate the bitmap by the specified angle
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        return bitmap // Return the bitmap without rotation if rotation is 0
    }
}
