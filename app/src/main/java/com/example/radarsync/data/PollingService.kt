package com.example.radarsync.data

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.radarsync.DependencyProvider
import com.example.radarsync.LOG_TAG
import com.example.radarsync.MainActivity
import com.example.radarsync.utilities.PositionLocationInterface
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class PollingService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var userLocation: Location
    private lateinit var positionRepository: PositionRepository
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
        positionRepository = DependencyProvider.getPositionRepository()

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

        // Update user location
        getLastKnownLocation()

        if(positionRepository.positionList.value == null) {
            Log.d(LOG_TAG, "No positions found")
            return
        }

        for(position in positionRepository.positionList.value!!) {
            val location = PositionLocationInterface.createLocationFromPosition(position)

            // Notify on 500m proximity. TODO: make this customizable per position in UI
            if(userLocation.distanceTo(location) < 500) {
                showNotification("User is within 500 meters of ${position.name}", "Current distance is ${userLocation.distanceTo(location)}")
            }
        }
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

    private fun getLastKnownLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(LOG_TAG, "No permissions to check location")
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Handle the location here
                if (location != null) {
                    userLocation = location
                }
            }
            .addOnFailureListener { e ->
                Log.e(LOG_TAG, "Location settings not satisfied: $e")
            }
    }

    enum class Actions {
        START,
        STOP
    }
}
