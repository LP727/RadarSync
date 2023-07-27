package com.example.radarsync

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.radarsync.data.PositionEntity
import com.example.radarsync.utilities.FileHelper
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainViewModel(app: Application) : AndroidViewModel(app) {
    val positionList = MutableLiveData<List<PositionEntity>>()
    private val listType = Types.newParameterizedType(
        List::class.java, PositionEntity::class.java // custom type for JSON parsing
    )

    init {
        val text = FileHelper.getTextFromAssets(app, "test_positions.json")

        parseTest(text)
    }

    fun parseTest(text: String)
    {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<List<PositionEntity>> = moshi.adapter(listType)
        val positionData = adapter.fromJson(text)

        for (position in positionData ?: emptyList()) {
            Log.i(
                LOG_TAG,
                "${position.name}, time: ${position.time}"
            )
        }
    }
}
