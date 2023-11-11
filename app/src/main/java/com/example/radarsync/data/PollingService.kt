package com.example.radarsync.data

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.radarsync.LOG_TAG
import com.example.radarsync.MainActivity
import com.example.radarsync.utilities.CryptoManager
import com.example.radarsync.utilities.FileHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class PollingService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var positionRepository: PositionRepository
    private lateinit var userSettings: UserSettings
    private val cryptoManager = CryptoManager()
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
        when (intent?.action) {
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        // Load user settings and initialize repository
        userSettings = FileHelper.loadUserSettings(this, cryptoManager)
        positionRepository = PositionRepository(this, userSettings)

        handler.post(fetchDataRunnable)

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Polling is active")
            .setContentText("Every 30 seconds")
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stop() {
        // Remove the callbacks to stop data fetching
        handler.removeCallbacks(fetchDataRunnable)

        // Stop the foreground service
        stopForeground(STOP_FOREGROUND_REMOVE)

        // Stop the service itself
        stopSelf()
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

        Log.d(LOG_TAG, "Fetching data")
        positionRepository.refreshData()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // TODO : Implement taking action on new data
        //showNotification("Data Fetched", "New data is available!")
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
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    enum class Actions {
        START,
        STOP
    }
}
