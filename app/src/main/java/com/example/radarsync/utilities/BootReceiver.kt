package com.example.radarsync.utilities

import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.radarsync.data.LocationUpdateWorker
import java.util.concurrent.TimeUnit

class BootReceiver {
    fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val periodicWorkRequest = PeriodicWorkRequestBuilder<LocationUpdateWorker>(
                repeatInterval = 15, // Repeat every 15 minutes
                repeatIntervalTimeUnit = TimeUnit.SECONDS
            )
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()

            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(
                "LocationUpdate",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                periodicWorkRequest
            )
        }
    }
}
