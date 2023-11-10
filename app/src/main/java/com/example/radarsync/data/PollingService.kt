package com.example.radarsync.data

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.radarsync.MainActivity

class PollingService : Service() {

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "polling_channel"
        private const val NOTIFICATION_ID = 1

        // Interval for data fetch in milliseconds (30 seconds)
        private const val FETCH_INTERVAL = 30 * 1000L
    }

    private val handler = Handler(Looper.getMainLooper())
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stopSelf()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {

        handler.post(fetchDataRunnable)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Polling is active")
            .setContentText("Every 30 seconds")
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private val fetchDataRunnable = object : Runnable {
        override fun run() {
            // Fetch your data here
            fetchData()

            // Schedule the next data fetch after the interval
            handler.postDelayed(this, FETCH_INTERVAL)
        }
    }

    private fun fetchData() {
        // Implement your logic to fetch data here
        // For example, you can invoke a repository method to fetch data
        // Update your database or perform any necessary actions
        showNotification("Data Fetched", "New data is available!")
    }


    private fun showNotification(title: String, message: String) {
        // Create an explicit intent for the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Build the notification
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss the notification when clicked
            .build()

        // Notify using the NotificationManager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    enum class Actions {
        START,
        STOP
    }
}
