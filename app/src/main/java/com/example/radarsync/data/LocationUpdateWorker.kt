package com.example.radarsync.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.radarsync.LOG_TAG
import com.example.radarsync.utilities.PermissionHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationUpdateWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    @SuppressLint("MissingPermission")
    override fun doWork(): Result {

        val permissionsToRequest = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )

        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        if (PermissionHelper.checkPermissions(permissionsToRequest, applicationContext)) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    LocationLiveDataRepository.updateLocation(location)
                    Log.d(
                        LOG_TAG, "Location: Lat=${location.latitude}, Long=${location.longitude}, " +
                            "Altitude=${location.altitude}, Accuracy=${location.accuracy}")
                }
                else
                {
                    Log.d(LOG_TAG, "Location is null")
                }
            }
        }

//        val database = PositionDatabase.getInstance(applicationContext)
//        val repository = PositionRepository(database.positionDao)
        return Result.success()
    }
}
