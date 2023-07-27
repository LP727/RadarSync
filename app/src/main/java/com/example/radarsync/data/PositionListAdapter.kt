package com.example.radarsync.data

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.radarsync.R
import com.example.radarsync.databinding.PosItemBinding
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class PositionListAdapter(private val positionList: List<PositionEntity>) :
    RecyclerView.Adapter<PositionListAdapter.PositionViewHolder>() {

    inner class PositionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = PosItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionViewHolder {
        val infalter = LayoutInflater.from(parent.context)
        val view = infalter.inflate(
            R.layout.pos_item,
            parent,
            false
        )
        return PositionViewHolder(view)
    }

    override fun getItemCount() = positionList.size

    override fun onBindViewHolder(holder: PositionViewHolder, position: Int) {
        val pos = positionList[position]
        val loc = createLocationFromPosition(pos)
        with(holder.binding) {
            nameText.text = pos.name
            //TODO : figure out how to calculate distance
            distanceText.text = "Unknown"

            // TODO : figure out if Glide and BindingAdapter are needed
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

    private fun createLocationFromPosition(pos: PositionEntity): Location {
        val Loc = Location(pos.name)
        Loc.latitude = pos.latitude
        Loc.longitude = pos.longitude
        Loc.altitude = pos.altitude
        Loc.accuracy = pos.accuracy
        Loc.time = pos.time
        return Loc
    }

    fun getTimeAndDateStringsFromTimestamp(unixTimestamp: Long): Pair<String, String> {
        // Convert the timestamp to a Date object
        val date = Date(unixTimestamp * 1000) // Multiply by 1000 to convert to milliseconds

        // Format the time string
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeString = timeFormat.format(date)

        // Format the date string
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormat.format(date)

        return Pair(timeString, dateString)
    }
}
