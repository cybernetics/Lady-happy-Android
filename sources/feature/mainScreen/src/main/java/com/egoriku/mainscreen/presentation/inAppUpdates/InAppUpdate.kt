package com.egoriku.mainscreen.presentation.inAppUpdates

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.egoriku.ladyhappy.extensions.Event
import com.egoriku.ladyhappy.extensions.logDm
import com.egoriku.mainscreen.presentation.inAppUpdates.InAppUpdateState.*
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

const val UPDATE_REQUEST_CODE = 6708

sealed class InAppUpdateState {
    object OnFailed : InAppUpdateState()
    object Downloaded : InAppUpdateState()
    data class RequestFlowUpdate(val updateInfo: AppUpdateInfo) : InAppUpdateState()
}

class InAppUpdate(context: Context) {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    private val _status = MutableLiveData<Event<InAppUpdateState>>()

    val status: LiveData<Event<InAppUpdateState>> = _status

    private val installStateUpdatedListener: InstallStateUpdatedListener = InstallStateUpdatedListener { installState ->
        when (installState.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                logDm("InstallStatus.DOWNLOADED")
                _status.value = Event(Downloaded)
            }
            InstallStatus.FAILED -> {
                logDm("InstallStatus.FAILED")
                _status.value = Event(OnFailed)
            }
            InstallStatus.CANCELED -> {
                logDm("InstallStatus.CANCELED")
            }
            InstallStatus.DOWNLOADING -> {
                logDm("InstallStatus.DOWNLOADING")
            }
            InstallStatus.INSTALLED -> {
                logDm("InstallStatus.INSTALLED")
            }
            InstallStatus.INSTALLING -> {
                logDm("InstallStatus.INSTALLING")
            }
            InstallStatus.PENDING -> {
                logDm("InstallStatus.PENDING")
            }
            InstallStatus.UNKNOWN -> {
                logDm("InstallStatus.UNKNOWN")
            }
        }
    }

    fun checkForUpdates() {
        logDm("init")
        appUpdateManager.appUpdateInfo
                .addOnSuccessListener { appUpdateInfo ->
                    when (appUpdateInfo.updateAvailability()) {
                        UpdateAvailability.UPDATE_AVAILABLE -> {
                            logDm("ON_CREATE: UpdateAvailability.UPDATE_AVAILABLE")
                            when {
                                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                                    logDm("AppUpdateType.FLEXIBLE")
                                    _status.value = Event(RequestFlowUpdate(appUpdateInfo))
                                }
                            }
                        }
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS -> {
                            logDm("ON_CREATE: UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS")
                            appUpdateManager.registerListener(installStateUpdatedListener)

                            logDm("ON_CREATE: install status: ${appUpdateInfo.installStatus()}")

                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                logDm("ON_CREATE: UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS - InstallStatus.DOWNLOADED")
                                _status.value = Event(Downloaded)
                            }
                        }
                        UpdateAvailability.UNKNOWN -> {
                            logDm("ON_CREATE: UpdateAvailability.UNKNOWN")
                        }
                        UpdateAvailability.UPDATE_NOT_AVAILABLE -> {
                            logDm("ON_CREATE: UpdateAvailability.UPDATE_NOT_AVAILABLE")
                        }
                    }
                }.addOnFailureListener {
                    logDm("addOnFailureListener $it")
                }
    }

    fun startUpdateFlow(activity: Activity, updateInfo: AppUpdateInfo) {
        logDm("startUpdateFlow")
        appUpdateManager.registerListener(installStateUpdatedListener)
        appUpdateManager.startUpdateFlowForResult(
                updateInfo,
                AppUpdateType.FLEXIBLE,
                activity,
                UPDATE_REQUEST_CODE
        )
    }

    fun unsubscribe() {
        logDm("unsubscribe")
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }

    fun completeUpdate() {
        logDm("completeUpdate")
        unsubscribe()
        appUpdateManager.completeUpdate()
    }
}