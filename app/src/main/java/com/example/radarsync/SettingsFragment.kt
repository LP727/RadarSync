package com.example.radarsync

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.radarsync.data.UserSettings
import com.example.radarsync.databinding.FragmentSettingsBinding
import com.example.radarsync.utilities.FileHelper

class SettingsFragment : Fragment() {
    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.saveButton.setOnClickListener {
            saveAndReturn()
        }
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        loadSettings()
        return binding.root
    }

    private fun loadSettings() {
        binding.editTextUrl.setText(viewModel.userSettings.url)
        binding.editTextPort.setText(viewModel.userSettings.port.toString())
        binding.editTextUsr.setText(viewModel.userSettings.username)
        binding.editTextTextPassword.setText(viewModel.userSettings.password)
    }

    private fun saveSettings() {

        val imm = requireActivity()
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(
            binding.saveButton.windowToken,
            0
        ) // close the keyboard as we leave the fragment

        val localSettings = UserSettings(
            binding.editTextUrl.text.toString(),
            binding.editTextPort.text.toString().toIntOrNull(),
            binding.editTextUsr.text.toString(),
            binding.editTextTextPassword.text.toString()
        )
        viewModel.userSettings = localSettings

        FileHelper.saveUserSettings(requireContext(), viewModel.cryptoManager, localSettings)
    }

    private fun saveAndReturn() {
        saveSettings()
        findNavController().navigateUp()
    }
}
