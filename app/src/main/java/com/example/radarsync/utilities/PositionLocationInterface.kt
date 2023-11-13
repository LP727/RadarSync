package com.example.radarsync.utilities

import android.location.Location
import com.example.radarsync.data.PositionEntity

class PositionLocationInterface {
    companion object {
        fun createLocationFromPosition(pos: PositionEntity): Location {
            val loc = Location(pos.name)
            loc.latitude = pos.latitude
            loc.longitude = pos.longitude
            loc.altitude = pos.altitude
            loc.accuracy = pos.accuracy
            loc.time = pos.time
            return loc
        }

        fun createPositionFromLocation(loc: Location, id: String, name: String): PositionEntity {
            return PositionEntity(
                id,
                loc.latitude,
                loc.longitude,
                loc.altitude,
                loc.accuracy,
                name,
                loc.time
            )
        }
    }
}
