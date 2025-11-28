package com.slings.vasantham

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.multidex.MultiDex
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class AppController : Application() {

    lateinit var firebaseCrashlytics: FirebaseCrashlytics

    @DelicateCoroutinesApi
    companion object Factory {
        var mInstance: AppController? = null
        fun get(context: Context): AppController = context.applicationContext as AppController

        @Synchronized
        fun getInstance(): AppController? {
            return mInstance
        }
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this);
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
//        firebaseCrashlytics = FirebaseCrashlytics.getInstance()
//        firebaseCrashlytics.setCrashlyticsCollectionEnabled(true)
//        firebaseCrashlytics.setUserId(Util.getPreference(applicationContext,"userId","").toString())
        mInstance = this

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().detectAll()
                .penaltyLog()
                .penaltyFlashScreen()
                .build(),
        )
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()

//                .penaltyDeath()
                .penaltyLog()
                .build(),
        )

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


    fun setConnectivityListener(listener: MyReceiver.ConnectivityReceiverListener?) {
        MyReceiver.connectivityReceiverListener = listener
    }
}