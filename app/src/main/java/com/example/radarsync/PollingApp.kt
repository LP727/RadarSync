package com.example.radarsync

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class PollingApp: Application() {
    override fun onCreate() {
        super.onCreate()

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
