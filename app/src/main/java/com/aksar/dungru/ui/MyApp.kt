package com.aksar.dungru.ui

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.aksar.dungru.R
import com.aksar.dungru.utils.NetworkUtils.IS_CONNECTED
import com.aksar.dungru.utils.NetworkUtils.isFirstTime
import com.aksar.dungru.utils.NetworkUtils.isNetworkAvailable
import com.aksar.dungru.utils.NetworkUtils.showInternetAvailableSnackBar
import com.aksar.dungru.utils.NetworkUtils.showNoInternetSnackBar
import java.security.MessageDigest

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        registerNetworkChangeReceiver()

        /** to find hash Key for facebook*/
/*        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {}*/

    }
    private fun registerNetworkChangeReceiver() {
        val networkChangeReceiver = NetworkChangeReceiver()
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }
    companion object {
        private var currentActivity: Activity? = null
        fun setCurrentActivity(activity: Activity?) {
            currentActivity = activity
        }

    }
    class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null) {
                if (isNetworkAvailable(context)) {
                    // Network is available
                    // Inform the user or update UI as needed
                    IS_CONNECTED = true
                    if(!isFirstTime){
                        currentActivity?.let { showInternetAvailableSnackBar(it) }
                    }
                    else
                        isFirstTime = !isFirstTime

                } else {
                    // Network is unavailable
                    // Inform the user or update UI as needed
                    if (isFirstTime) {
                        isFirstTime = !isFirstTime
                    }
                    IS_CONNECTED = false
                    currentActivity?.let { showNoInternetSnackBar(it) }
                }
            }
        }
    }
}
