package com.example.radarsync.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "position")
data class PositionEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    @Json(name = "lat") val latitude: Double,
    @Json(name = "lon") val longitude: Double,
    @Json(name = "alt") val altitude: Double,
    @Json(name = "acc") val accuracy: Float,
    val name: String,
    @Json(name = "tim") val time: Long,
)
