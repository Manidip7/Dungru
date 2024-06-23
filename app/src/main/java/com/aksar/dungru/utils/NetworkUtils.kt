package com.aksar.dungru.utils

import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.FrameMetrics
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresPermission
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.aksar.dungru.R
import com.aksar.dungru.ui.MyApp
import com.google.android.material.snackbar.Snackbar

object NetworkUtils{
    var IS_CONNECTED = false
    var snackbar: Snackbar? = null
    var isFirstTime = true
    @RequiresPermission(permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    fun showNoInternetSnackBar(context: Context) {
        val rootView = (context as Activity).findViewById<View>(android.R.id.content)
        snackbar = Snackbar.make(rootView, R.string.no_network, Snackbar.LENGTH_INDEFINITE)
        val params = snackbar!!.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        snackbar?.view?.layoutParams = params
//        snackbar?.setBackgroundTint(ContextCompat.getColor(context, R.color.action_color))
        snackbar?.show()

    }
    fun showInternetAvailableSnackBar(context: Context) {
        val rootView = (context as Activity).findViewById<View>(android.R.id.content)
        snackbar?.dismiss()
        snackbar = Snackbar.make(rootView, R.string.network_connected, Snackbar.LENGTH_SHORT)
        val params = snackbar!!.view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        snackbar?.view?.layoutParams = params
//        snackbar?.setBackgroundTint(ContextCompat.getColor(context, R.color.green))
        snackbar?.show()
    }
    fun checkConnection(activity: Activity) {
        MyApp.setCurrentActivity(activity)
        if(!IS_CONNECTED) showNoInternetSnackBar(activity)
    }

}
