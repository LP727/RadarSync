package com.example.radarsync

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radarsync.databinding.FragmentMainBinding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.radarsync.data.PositionListAdapter
import com.example.radarsync.utilities.TimeUtilities

class MainFragment : Fragment() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: PositionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]

        with(binding.recyclerView) {
            setHasFixedSize(true)
            val divider = DividerItemDecoration(
                context,
                LinearLayoutManager(context).orientation
            )
            addItemDecoration(divider)
        }

        viewModel.positionList.observe(
            viewLifecycleOwner, Observer
            {
                adapter = PositionListAdapter(it, viewModel.currentPosition.value)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(activity)
            }
        )

        // observe the current position
        viewModel.currentPosition.observe(
            viewLifecycleOwner, Observer
            {
                binding.userPos.latitudeText.text = Location.convert(it.latitude, Location.FORMAT_DEGREES)
                binding.userPos.longitudeText.text = Location.convert(it.longitude, Location.FORMAT_DEGREES)
                binding.userPos.altitudeText.text = Location.convert(it.altitude, Location.FORMAT_DEGREES)
                binding.userPos.accuracyText.text = it.accuracy.toString()

                val (timeString, dateString) = TimeUtilities.getTimeAndDateStringsFromTimestamp(it.time)
                binding.userPos.latestTimeText.text = timeString
                binding.userPos.latestDateText.text = dateString
            }
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost = requireActivity()

        // Menu code
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (R.id.action_settings == menuItem.itemId) {
                    return editSettings()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        // By calling addMenuProdiver with a Lifecyle, the menu will be added and removed automatically
    }

    private fun editSettings(): Boolean {
        findNavController().navigate(R.id.nav_to_action_settings)
        return true
    }
}
