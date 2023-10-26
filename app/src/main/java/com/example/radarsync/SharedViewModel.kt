package com.example.radarsync

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.radarsync.data.PositionEntity
import com.example.radarsync.data.UserSettings
import com.example.radarsync.utilities.CryptoManager
import com.example.radarsync.utilities.FileHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SharedViewModel(val app: Application) : AndroidViewModel(app) {
    val positionList = MutableLiveData<MutableList<PositionEntity>>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val currentPosition = MutableLiveData<Location>()
    val cryptoManager = CryptoManager()
    var userSettings: UserSettings
    private val listType = Types.newParameterizedType(
        List::class.java, PositionEntity::class.java // custom type for JSON parsing
    )

    init {
        val text = FileHelper.getTextFromAssets(app, "test_positions.json")

        // Load user settings from encrypted file
        userSettings = FileHelper.loadUserSettings(app, cryptoManager)
        positionList.value = parseText(text)
        requestLocationUpdates()
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
    private fun parseText(text: String): MutableList<PositionEntity>
    {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<MutableList<PositionEntity>> = moshi.adapter(listType)
        val positionData = adapter.fromJson(text)

        for (position in positionData ?: emptyList()) {
            Log.i(
                LOG_TAG,
                "${position.name}, time: ${position.time}"
            )
        }

        return positionData ?: mutableListOf<PositionEntity>()
    }
}
