package com.example.radarsync

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class RadarSyncApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyProvider.initialize(this)

        val channel = NotificationChannel(
            "polling_channel",
            "Radar Sync Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        // OS service, not foreground service
        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }
}
