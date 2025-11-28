package com.slings.vasantham.support

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.slings.vasantham.R


object InAppUpdate {
    var REQUEST_APP_UPDATE = 302
    fun setImmediateUpdate(appUpdateManager: AppUpdateManager, activity: Activity?) {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->

                // Checks that the platform will allow the specified type of update.
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    // Request the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            activity!!,
                            REQUEST_APP_UPDATE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
    }

    fun setImmediateUpdateOnResume(appUpdateManager: AppUpdateManager, activity: Activity?) {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            activity!!,
                            REQUEST_APP_UPDATE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
    }

    fun setFlexibleUpdate(appUpdateManager: AppUpdateManager, activity: Activity) {
        val installStateUpdatedListener = InstallStateUpdatedListener { installState: InstallState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                val snackbar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    activity.getString(R.string.in_app_snack_bar_message),
                    Snackbar.LENGTH_INDEFINITE
                )
                //lambda operation used for below action
                snackbar.setAction(
                    activity.getString(R.string.in_app_snack_bar_action_title)
                ) { view: View? -> appUpdateManager.completeUpdate() }
                snackbar.setActionTextColor(activity.resources.getColor(R.color.in_app_snack_bar_text_color))
                snackbar.show()
            } else Log.e("UPDATE", "Not downloaded yet")
        }
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->

                // Checks that the platform will allow the specified type of update.
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    // Request the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            activity,
                            REQUEST_APP_UPDATE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    fun setFlexibleUpdateOnResume(appUpdateManager: AppUpdateManager, activity: Activity?) {
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            activity!!,
                            REQUEST_APP_UPDATE
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    }
                }
            }
    }
}