package com.example.radarsync.data

import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Pass in port, url, username, and password to the constructor
// Class that will be used to access the database to fetch positions (Make Object (singleton) instead?)
class PositionRepository(val app: Application) {
    // TODO: create a local positionDao as well as a local position databse
    //private val positionDao = PositionDatabase.getDatabase(app).positionDao()

    // TODO: Remove list from SharedViewModel and replace with this repository
    private val positionList = MutableLiveData<MutableList<PositionEntity>>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // TODO: Attempt to fetch data from local database first once implemented
            callWebService()
        }
    }

    private fun callWebService() {
        TODO("Not yet implemented")
    }

    fun refreshData() {
        CoroutineScope(Dispatchers.IO).launch {
            callWebService()
        }
    }
}
