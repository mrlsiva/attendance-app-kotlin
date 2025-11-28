package com.slings.vasantham

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import kotlinx.coroutines.DelicateCoroutinesApi

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        mcontext = context
        val action = intent.action
        if ("android.net.conn.CONNECTIVITY_CHANGE" == action) {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = (activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting)
            if (connectivityReceiverListener != null) {
                connectivityReceiverListener!!.onNetworkConnectionChanged(isConnected)
            }
        }
    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    @DelicateCoroutinesApi
    companion object {
        @SuppressLint("StaticFieldLeak")
        var mcontext: Context? = null
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
        val isConnected: Boolean
            get() {
                val cm =
                    AppController.getInstance()?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                return (activeNetwork != null
                        && activeNetwork.isConnectedOrConnecting)
            }
    }
}