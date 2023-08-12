package com.example.radarsync.data

import android.location.Location
import androidx.lifecycle.MutableLiveData

object LocationLiveDataRepository {
    private val currentLocation = MutableLiveData<Location>()

    fun getLocation(): MutableLiveData<Location> {
        return currentLocation
    }

    fun updateLocation(location: Location) {
        currentLocation.postValue(location)
    }
}
