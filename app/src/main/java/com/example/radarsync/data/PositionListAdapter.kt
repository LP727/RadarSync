package com.example.radarsync.data

import android.annotation.SuppressLint
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.radarsync.R
import com.example.radarsync.databinding.PosItemBinding
import com.example.radarsync.utilities.PositionLocationInterface
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class PositionListAdapter(private val positionList: List<PositionEntity>, private val userLoc: Location?) :
    RecyclerView.Adapter<PositionListAdapter.PositionViewHolder>() {

    inner class PositionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = PosItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(
            R.layout.pos_item,
            parent,
            false
        )
        return PositionViewHolder(view)
    }

    override fun getItemCount() = positionList.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: PositionViewHolder, position: Int) {
        val pos = positionList[position]
        val loc = PositionLocationInterface.createLocationFromPosition(pos)
        with(holder.binding) {
            nameText.text = pos.name

            val displayedDistance : String
            val distance = if (userLoc!= null) loc.distanceTo(userLoc) else null
            if(distance != null) {
                val distanceKm = distance.div(1000)
                displayedDistance = "${distanceKm}km"
                if(distance < 500) {
                    distanceText.setTextColor(R.color.teal_200)
                }
            } else {
                displayedDistance = "Unknown"
            }

            distanceText.text = displayedDistance
            latitudeText.text = Location.convert(pos.latitude, Location.FORMAT_DEGREES)
            longitudeText.text = Location.convert(pos.longitude, Location.FORMAT_DEGREES)
            altitudeText.text = Location.convert(pos.altitude, Location.FORMAT_DEGREES)
            accuracyText.text = loc.accuracy.toString()

            // Compute latest time and date from unix timestamp
            val (timeString, dateString) = getTimeAndDateStringsFromTimestamp(loc.time)
            latestTimeText.text = timeString
            latestDateText.text = dateString
        }
    }

    private fun getTimeAndDateStringsFromTimestamp(unixTimestamp: Long): Pair<String, String> {
        // Convert the timestamp to a Date object
        val date = Date(unixTimestamp) // Multiply by 1000 to convert to milliseconds

        // Format the time string
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeString = timeFormat.format(date)

        // Format the date string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(date)

        return Pair(timeString, dateString)
    }
}
