package com.example.radarsync

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.radarsync.data.PositionEntity
import com.example.radarsync.data.PositionRepository
import com.example.radarsync.data.UserSettings
import com.example.radarsync.utilities.CryptoManager
import com.example.radarsync.utilities.FileHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class SharedViewModel(val app: Application) : AndroidViewModel(app) {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val currentPosition = MutableLiveData<Location>()
    val cryptoManager = CryptoManager()
    var userSettings: UserSettings

    private var dataRepo: PositionRepository
    var positionList: MutableLiveData<MutableList<PositionEntity>>

    init {
        requestLocationUpdates()

        // Load user settings from encrypted file
        userSettings = FileHelper.loadUserSettings(app, cryptoManager)
        dataRepo = DependencyProvider.getPositionRepository()
        dataRepo.updateSettings(userSettings)
        positionList = dataRepo.positionList
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentPosition.value = locationResult.lastLocation
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(app)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }
}
