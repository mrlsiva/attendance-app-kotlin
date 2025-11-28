package com.slings.vasantham

import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
open class BaseActivity : AppCompatActivity(), MyReceiver.ConnectivityReceiverListener {

    companion object {
        var isActive: Boolean = false
    }


    private var isConnected = false
    private var myReceiver: MyReceiver? = null
    private var title = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
       /* if (isTablet()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }*/
        if (this.javaClass.simpleName.equals("HomeActivity")) {
            Handler(Looper.myLooper()!!).postDelayed(
                {
                    StatusTheme()
                },
                500
            )
        } else {
            StatusTheme()
        }
        myReceiver = MyReceiver()
        checkConnection()
    }

    private fun StatusTheme() {
       /* val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.white)
        val background: Drawable? = ContextCompat.getDrawable(this, R.drawable.statusbar_bg)
        window.setBackgroundDrawable(background)*/
    }

 /*   private fun isTablet(): Boolean {
        return resources.getBoolean(R.bool.landscape_only)
    }*/

    private fun checkConnection() {
        isConnected = MyReceiver.isConnected
        if (isConnected) {
            if (isActive) {
                NoInternetActivity.NoInActivity?.finish()
            }
        } else {
            val intent = Intent(applicationContext, NoInternetActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        checkConnection()
    }


    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(myReceiver, filter)
        AppController.getInstance()!!.setConnectivityListener(this)
        val className: String = this.javaClass.name.substring(
            this.javaClass.name.lastIndexOf(".") + 1, this.javaClass.name.length
        )
//
//        firebase.setPageTitle(title, applicationContext)
        /*for registering to broadcast receiver*/
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (myReceiver != null) {
                unregisterReceiver(myReceiver)
            }
        } catch (e: Exception) {
            AppController.mInstance
        }
    }
}