package com.example.radarsync

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.radarsync.databinding.FragmentMainBinding
import androidx.lifecycle.Observer
import com.example.radarsync.data.PositionEntity
import com.example.radarsync.data.PositionListAdapter

class MainFragment : Fragment() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: PositionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        val menuHost = requireActivity()

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
                adapter = PositionListAdapter(it)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(activity)
            }
        )

        // observe the current position
        viewModel.currentPosition.observe(
            viewLifecycleOwner, Observer
            {
                val newPosition = PositionEntity(
                    "random id",
                    it.latitude,
                    it.longitude,
                    it.altitude,
                    it.accuracy,
                    "Me",
                    it.time
                )

                val currentList = viewModel.positionList.value ?: mutableListOf<PositionEntity>()
                // Check if the id is already in the list
                val index = currentList.indexOfFirst { pos -> pos.id == newPosition.id }
                if (index == -1) {
                    currentList.add(newPosition)
                } else {
                    currentList[index] = newPosition
                }
                viewModel.positionList.value = currentList
            }
        )

        // Menu code
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return true
            }
        })

        return binding.root
    }
}
