package com.example.radarsync

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radarsync.databinding.FragmentMainBinding
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.radarsync.data.PollingService
import com.example.radarsync.data.PositionListAdapter
import com.example.radarsync.utilities.TimeUtilities

class MainFragment : Fragment() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: PositionListAdapter

    // Preferences for the switch
    private lateinit var switch: SwitchCompat
    private val PREFS_NAME = "SwitchPrefs"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        switch = binding.pollSwitch

        // Upon Creation, load the switch state from the shared preferences
        val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val switchState = sharedPreferences.getBoolean("switch_state", false)
        switch.isChecked = switchState
        if (switchState) {
            val startIntent = Intent(requireContext(), PollingService::class.java).apply {
                action = PollingService.Actions.START.toString()
            }
            requireContext().startService(startIntent)
        }

        switch.setOnCheckedChangeListener { _, isChecked ->
            // Save switch state in SharedPreferences when it changes
            val editor = sharedPreferences.edit()
            editor.putBoolean("switch_state", isChecked)
            editor.apply()

            if(isChecked) {
                Toast.makeText(context, "Polling is active", Toast.LENGTH_SHORT).show()
                val startIntent = Intent(requireContext(), PollingService::class.java).apply {
                    action = PollingService.Actions.START.toString()
                }
                requireContext().startService(startIntent)
            } else {
                Toast.makeText(context, "Polling is inactive", Toast.LENGTH_SHORT).show()
                val stopIntent = Intent(requireContext(), PollingService::class.java).apply {
                    action = PollingService.Actions.STOP.toString()
                }
                requireContext().startService(stopIntent)
            }
        }

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

    override fun onPause() {
        super.onPause()
        val switchState = switch.isChecked
        saveSwitchState(requireContext(), switchState)
    }

    override fun onResume() {
        super.onResume()
        val switchState = loadSwitchState(requireContext())
        switch.isChecked = switchState
    }
    private fun saveSwitchState(context: Context, state: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("switch_state", state)
        editor.apply()
    }

    private fun loadSwitchState(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("switch_state", false)
    }
    private fun editSettings(): Boolean {
        findNavController().navigate(R.id.nav_to_action_settings)
        return true
    }
}
