package com.aksar.dungru.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Utils(val context: Context) {
    fun isDarkTheme(): Boolean {
        val currentNightMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    fun requestPermission(permissionArray: Array<String>, reqCode: Int) {
        (context as Activity).requestPermissions(
            permissionArray,
            reqCode
        )
    }


    fun isAllPermissionsGranted(permissionArray: Array<String>): Boolean {
        for (permission in permissionArray) {
            if (ContextCompat.checkSelfPermission(
                    (context as Activity),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

}